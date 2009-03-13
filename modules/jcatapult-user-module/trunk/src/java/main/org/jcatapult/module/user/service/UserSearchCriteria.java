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
package org.jcatapult.module.user.service;

import org.jcatapult.crud.service.AbstractSearchCriteria;
import org.jcatapult.user.domain.User;

/**
 * <p>
 * This is the criteria used for the User admin.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class UserSearchCriteria extends AbstractSearchCriteria<User> {
    private final Class userType;
    private String firstName;
    private String lastName;
    private String companyName;
    private String login;

    public UserSearchCriteria(Class userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    protected void buildJPAQuery(QueryBuilder builder) {
        // Using default select and sort by
        addWhere(builder);
    }

    protected void buildJPACountQuery(QueryBuilder builder) {
        // Using default select
        addWhere(builder);
    }

    private void addWhere(QueryBuilder builder) {
        if (firstName != null) {
            builder.andWhere("e.name.firstName like :firstName");
            builder.withParameter("firstName", "%" + firstName + "%");
        }

        if (lastName != null) {
            builder.andWhere("e.name.lastName like :lastName");
            builder.withParameter("lastName", "%" + lastName + "%");
        }

        if (companyName != null) {
            builder.andWhere("e.companyName like :companyName");
            builder.withParameter("companyName", "%" + companyName + "%");
        }

        if (login != null) {
            builder.andWhere("e.login like :login");
            builder.withParameter("login", "%" + login + "%");
        }
    }

    public Class<User> getResultType() {
        return userType;
    }
}