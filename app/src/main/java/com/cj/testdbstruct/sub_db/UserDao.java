package com.cj.testdbstruct.sub_db;

import com.cj.testdbstruct.db.BaseDao;
import com.cj.testdbstruct.entity.User;

/**
 * Author: cj
 * Create time: 2019/5/7 16:05
 */
public class UserDao extends BaseDao<User> {
    public void login(User user) {
        insert(user);
    }
}
