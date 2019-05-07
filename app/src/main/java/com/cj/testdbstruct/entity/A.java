package com.cj.testdbstruct.entity;

import com.cj.testdbstruct.anno.FieldName;
import com.cj.testdbstruct.anno.TableName;

/**
 * Author: cj
 * Create time: 2019/4/29 17:59
 */
@TableName("person")
public class A {
    @FieldName("_id")
    private int id;
    private String name;

    public A() {}

    public A(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "A{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
