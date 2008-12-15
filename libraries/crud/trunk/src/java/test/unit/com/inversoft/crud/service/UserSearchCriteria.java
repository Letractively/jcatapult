/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.crud.service;

import net.java.lang.StringTools;

import com.inversoft.crud.domain.User;

/**
 * <p>
 * This is a test criteria.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class UserSearchCriteria extends AbstractSearchCriteria<User> {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    protected void buildJPAQuery(QueryBuilder builder) {
        builder.select("u from User u");
        addWhereClause(builder);
    }

    protected void buildJPACountQuery(QueryBuilder builder) {
        builder.select("count(u) from User u");
        addWhereClause(builder);
    }

    private void addWhereClause(QueryBuilder builder) {
        if (!StringTools.isTrimmedEmpty(name)) {
            builder.where("u.name like :name");
            builder.withParameter("name", name);
        }

        if (age != null) {
            builder.andWhere("u.age < :age");
            builder.withParameter("age", age);
        }
    }

    public Class<User> getResultType() {
        return User.class;
    }
}