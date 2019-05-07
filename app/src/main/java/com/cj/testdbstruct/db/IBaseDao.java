package com.cj.testdbstruct.db;

import android.database.Cursor;

import java.util.List;

/**
 * Author: cj
 * Create time: 2019/4/29 16:23
 * 数据库操作接口
 */
public interface IBaseDao<T> {
    /**
     * 插入数据
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * 删除数据
     * @param where 条件
     * @return
     */
    int delete(T where);

    /**
     * 查询数据
     * @param where
     * @return
     */
    List<T> query(T where);
}
