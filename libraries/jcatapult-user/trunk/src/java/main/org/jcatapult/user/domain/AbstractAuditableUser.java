/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.user.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.AuditableSoftDeletableImpl;
import org.joda.time.DateTime;

/**
 * <p>
 * This class is a mappable super class that provides standard columns for
 * most of the User methods. This doesn't add support for roles, because it
 * is a many to many relationship in most cases and that requires the knowledge
 * of the concrete Role class. Also, some JPA implementations don't allow
 * mapped superclasses to contain collections.
 * </p>
 *
 * <p>
 * This also provides support for the auditable interface.
 * </p>
 *
 * <p>
 * This class uses a single column for emails and logins.
 * </p>
 *
 * <p>
 * The columns are as follows:
 * </p>
 *
 * <table border="1">
 * <tr><th>Name</th><th>Type</th><th>Description</th><th>Required?</th><th>Unique?</th><th>Additional info/constraints</th></tr>
 * <tr><td>login</td><td>varchar(255)</td><td>The login of the user.</td><td>Yes</td><td>Yes</td><td>None</td></tr>
 * <tr><td>password</td><td>varchar(255)</td><td>The password of the user.</td><td>Yes</td><td>No</td><td>None</td></tr>
 * <tr><td>guid</td><td>varchar(255)</td><td>A GUID used for password reset.</td><td>No</td><td>Yes</td><td>None</td></tr>
 * <tr><td>locked</td><td>boolean(or bit)</td><td>The locked flag.</td><td>Yes</td><td>No</td><td>None</td></tr>
 * <tr><td>expired</td><td>boolean(or bit)</td><td>The expired flag.</td><td>Yes</td><td>No</td><td>None</td></tr>
 * <tr><td>password_expired</td><td>boolean(or bit)</td><td>The password expired flag.</td><td>Yes</td><td>No</td><td>None</td></tr>
 * <tr><td>partial</td><td>boolean(or bit)</td><td>The partial flag.</td><td>Yes</td><td>No</td><td>None</td></tr>
 * <tr><td>verified</td><td>boolean(or bit)</td><td>The verified flag.</td><td>Yes</td><td>No</td><td>None</td></tr>
 * </table>
 *
 * @author  Brian Pontarelli
 */
@MappedSuperclass
public abstract class AbstractAuditableUser<T extends Role> extends AuditableSoftDeletableImpl implements AuditableUser<T> {
    @Required
    @Column(nullable = false, unique = true)
    private String login;

    // Not required because there will be a confirm and encryption handling that need to occur
    // before the password is set onto the entity for persistence
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String guid;

    @Column
    private boolean locked;

    @Column
    private boolean expired;

    @Column(name = "password_expired")
    private boolean passwordExpired;

    @Column
    private boolean partial;

    @Column
    private boolean verified;

    @Column(name = "last_login")
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime lastLogin;

    /**
     * {@inheritDoc}
     */
    public String getLogin() {
        return login;
    }

    /**
     * {@inheritDoc}
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * {@inheritDoc}
     */
    public String getEmail() {
        return login;
    }

    /**
     * {@inheritDoc}
     */
    public void setEmail(String email) {
        this.login = email;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return guid;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * {@inheritDoc}
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * {@inheritDoc}
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    /**
     * {@inheritDoc}
     */
    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPartial() {
        return partial;
    }

    /**
     * {@inheritDoc}
     */
    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * {@inheritDoc}
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * {@inheritDoc}
     */
    public DateTime getLastLogin() {
        return lastLogin;
    }

    /**
     * {@inheritDoc}
     */
    public void setLastLogin(DateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * This compares just based on the login.
     *
     * @param   o The other object to compare to.
     * @return  True if they are both Users and the logins are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User that = (User) o;

        return login.equals(that.getLogin());
    }

    /**
     * This uses just the login for hashing.
     *
     * @return  The hash code of the login.
     */
    @Override
    public int hashCode() {
        return login.hashCode();
    }

    /**
     * Uses the login to compare.
     *
     * @param   o The other user.
     * @return  The comparison of just the logins.
     */
    public int compareTo(User o) {
        return login.compareTo(o.getLogin());
    }
}