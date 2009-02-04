/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 */
package org.jcatapult.crud.service;

import org.jcatapult.crud.domain.User;

import net.java.lang.StringTools;

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