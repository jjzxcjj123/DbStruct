package com.cj.testdbstruct.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.cj.testdbstruct.anno.FieldName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: cj
 * Create time: 2019/5/7 9:54
 */
public class BaseDao<T> implements IBaseDao<T> {
    private static final String TAG = "BaseDao";

    private SQLiteDatabase mSQLiteDatabase;
    private Class<T> mClass;
    private String mTableName;
    private Map<String, Field> mCacheMap;

    protected void init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass, String tableName) {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            Log.e(TAG, "init: sqlite is null");
            return;
        }
        mSQLiteDatabase = sqLiteDatabase;
        mClass = entityClass;
        mTableName = tableName;
        // 创建表
        String createTableSql = getCreateTableSql();
        Log.i(TAG, "init: 建表 " + createTableSql);
        sqLiteDatabase.execSQL(createTableSql);
        initCache();
    }

    private void initCache() {
        mCacheMap = new HashMap<>();
        Field[] fields = mClass.getDeclaredFields();
        for (Field field : fields) {
            if (isInvalidField(field)) continue;
            field.setAccessible(true);
            String key = getColumnName(field);
            mCacheMap.put(key, field);
        }
        Log.i(TAG, "initCache: cache map size=" + mCacheMap.size());
    }

    private String getCreateTableSql() {
        // create table if not exists table(a TEXT,b NUMBER);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(mTableName);
        stringBuffer.append("(");
        Field[] fields = mClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (type == int.class
                    || type == double.class
                    || type == float.class
                    || type == Integer.class
                    || type == Double.class
                    || type == Float.class) {
                stringBuffer.append(getColumnName(field) + " NUMBER,");
            } else if (type == String.class) {
                stringBuffer.append(getColumnName(field) + " TEXT,");
            } else if (type == byte[].class) {
                stringBuffer.append(getColumnName(field) + " BLOB,");
            } else {
                // 不支持类型
                continue;
            }
        }
        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    @Override
    public long insert(T entity) {
        Map map = getMapValue(entity);
        ContentValues values = getContentValues(map);
        Log.i(TAG, "insert: 插入数据列数 " + values.size());
        return mSQLiteDatabase.insert(mTableName, null, values);
    }

    @Override
    public int delete(T where) {
        Condition condition = new Condition(where);
        Log.i(TAG, "delete: clause=" + condition.whereClause + "  args=" + condition.whereArgs);
        return mSQLiteDatabase.delete(mTableName, condition.whereClause, condition.whereArgs);
    }

    @Override
    public List<T> query(T where) {
        Cursor cursor = mSQLiteDatabase.query(mTableName, null, "name=?", new String[]{"sad"}, null, null, null);
        return getEntityList(where, cursor);
    }

    private List<T> getEntityList(T where, Cursor cursor) {
        List<T> res = new ArrayList<>();
        Object obj = null;
        while (cursor.moveToNext()) {
            try {
                obj = where.getClass().newInstance();
                Iterator iterator = mCacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = (Map.Entry<String, Field>) iterator.next();
                    String columnName = entry.getKey();
                    int columnIndex = cursor.getColumnIndex(columnName);
                    Field field = entry.getValue();

                    field.setAccessible(true);
                    Class type = field.getType();
                    if (type == String.class) {
                        field.set(obj, cursor.getString(columnIndex));
                    } else if (type == int.class || type == Integer.class) {
                        field.setInt(obj, cursor.getInt(columnIndex));
                    } else if (type == double.class || type == Double.class) {
                        field.setDouble(obj, cursor.getDouble(columnIndex));
                    } else if (type == byte[].class) {
                        field.set(obj, cursor.getBlob(columnIndex));
                    } else {
                        // 不支持类型
                        continue;
                    }
                }
                res.add((T) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return res;
    }

    /**
     * 条件内部类
     */
    private class Condition {
        public String whereClause;
        public String[] whereArgs;
        public Condition(T where) {
            Iterator iterator = getMapValue(where).entrySet().iterator();
            StringBuffer clauseBuffer = new StringBuffer();
            List<String> argList = new ArrayList<>();
            clauseBuffer.append("1=1");
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                clauseBuffer.append(" and " + entry.getKey() + "=?");
                argList.add(entry.getValue());
            }
            this.whereClause = clauseBuffer.toString();
            this.whereArgs = argList.toArray(new String[argList.size()]);
        }
    }

    private Map<String, String> getMapValue(T entity) {
        Map<String, String> map = new HashMap<>();
        Set<Map.Entry<String, Field>> entrySet = mCacheMap.entrySet();
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Field> entry = (Map.Entry<String, Field>) iterator.next();
            String key = entry.getKey();
            Field field = entry.getValue();
            try {
                Object obj = field.get(entity);
                if (obj != null) {
                    String value = obj.toString();
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                        map.put(key, value);
                    }
                } else {
                    continue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues values = new ContentValues();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                values.put(key, value);
            }
        }
        return values;
    }

    /**
     * 是否是有效的属性
     * @param field
     * @return
     */
    private boolean isInvalidField(Field field) {
        return field == null || field.getName().equals("serialVersionUID") || field.getName().equals("$change");
    }

    /**
     * 获取字段名
     * @param field
     * @return
     */
    private String getColumnName(Field field) {
        if (field.getAnnotation(FieldName.class) != null) {
            return field.getAnnotation(FieldName.class).value();
        } else {
            return field.getName();
        }
    }
}
