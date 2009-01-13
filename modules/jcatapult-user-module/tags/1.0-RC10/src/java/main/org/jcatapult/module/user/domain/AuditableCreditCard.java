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
package org.jcatapult.module.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.Type;
import org.jcatapult.domain.commerce.CreditCard;
import org.jcatapult.persistence.domain.Auditable;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.security.SecurityContext;
import org.joda.time.DateTime;

/**
 * <p>
 * This class stores the credit card information used during transaction
 * processing. This copies many of the identifiable and auditable code because
 * Java doesn't have mixins or multiple inheritance and this is a credit card,
 * identifiable and auditable.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name="credit_cards")
public class AuditableCreditCard extends CreditCard implements Identifiable, Auditable {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue
    private Integer id;

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

    @ManyToOne()
    @JoinColumn(name = "users_id")
    private DefaultUser user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public DefaultUser getUser() {
        return user;
    }

    public void setUser(DefaultUser user) {
        this.user = user;
    }
}