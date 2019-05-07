package com.cj.testdbstruct.entity;

/**
 * Author: cj
 * Create time: 2019/5/7 16:06
 */
public class User {
    private int id;
    private String name;
    private String password;

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
