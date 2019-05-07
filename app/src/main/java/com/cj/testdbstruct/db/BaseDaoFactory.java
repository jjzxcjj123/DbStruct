package com.cj.testdbstruct.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cj.testdbstruct.anno.TableName;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: cj
 * Create time: 2019/4/29 16:29
 */
public class BaseDaoFactory {
    String TAG = "BaseDaoFactory";
    private static final BaseDaoFactory instance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    private SQLiteDatabase mSqLiteDatabase;

    private Map<String, BaseDao> maps = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    private BaseDaoFactory() {
        String sqLiteDatabasePath = "/data/data/com.cj.testdbstruct";
        String sqLiteDatabaseName = "cj.db";
        Log.i(TAG, "BaseDaoFactory: " + sqLiteDatabasePath + File.separator + sqLiteDatabaseName);
        File folder = new File(sqLiteDatabasePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(sqLiteDatabasePath + File.separator + sqLiteDatabaseName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLiteDatabasePath + File.separator + sqLiteDatabaseName, null);
    }

    public <T extends BaseDao<M>, M> T getDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (maps.get(getTableName(entityClass)) != null) {
            return (T) maps.get(getTableName(entityClass));
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(mSqLiteDatabase, entityClass, getTableName(entityClass));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    /**
     * 获取表名
     * @param entity
     * @param <M>
     * @return
     */
    private <M> String getTableName(Class<M> entity) {
        if (entity.getAnnotation(TableName.class) != null) {
            return entity.getAnnotation(TableName.class).value();
        } else {
            return entity.getSimpleName();
        }
    }
}
