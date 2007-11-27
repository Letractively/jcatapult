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
 */
package org.jcatapult.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import org.jcatapult.security.SecurityContext;

/**
 * <p>
 * This class is an auditing persistable Object instance. Since Java is
 * lacking multiple inheritance I had to force this to extend Identifiable
 * since that will be the most common case. This does however mean that
 * Auditable will not have the ability to handle objects with natural keys
 * or other types of keys.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@MappedSuperclass
public abstract class AuditableImpl extends IdentifiableImpl implements Auditable {
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(name="insert_date")
    private DateTime insertDate;
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(name="update_date")
    private DateTime updateDate;
    @Column(name="insert_user")
    private String insertUser;
    @Column(name="update_user")
    private String updateUser;

    public DateTime getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(DateTime insertDate) {
        this.insertDate = insertDate;
    }

    public DateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getInsertUser() {
        return insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @PrePersist
    public void preInsert() {
        insertDate = new DateTime();
        insertUser = SecurityContext.getCurrentUsername();
        preUpdate();
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = new DateTime();
        updateUser = SecurityContext.getCurrentUsername();
    }
}