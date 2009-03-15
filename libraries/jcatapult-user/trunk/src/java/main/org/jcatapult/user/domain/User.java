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
package org.jcatapult.user.domain;

import java.io.Serializable;
import java.util.Set;

import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.domain.SoftDeletable;

/**
 * <p>
 * This interface defines a simple contract that most applications can
 * use for user's to register, login, etc. It also provides a nice standard
 * API for libraries, modules, and services to interact with respect to
 * users.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface User<T extends Role> extends Identifiable, SoftDeletable, Comparable<User>, Serializable {
    /**
     * @return  The users login.
     */
    String getLogin();

    /**
     * Sets the users login.
     *
     * @param   login The login.
     */
    void setLogin(String login);

    /**
     * @return  The user's email, which in some cases will be the same as the login.
     */
    String getEmail();

    /**
     * Sets the users email.
     *
     * @param   email The email.
     */
    void setEmail(String email);

    /**
     * @return  The roles for the user.
     */
    Set<T> getRoles();

    /**
     * Sets the roles for the user.
     *
     * @param   roles The roles.
     */
    void setRoles(Set<T> roles);

    /**
     * Adds a role to the user.
     *
     * @param   role The role to add.
     */
    void addRole(T role);

    /**
     * @return  The users current password.
     */
    String getPassword();

    /**
     * Sets the users current password.
     *
     * @param   password The password.
     */
    void setPassword(String password);

    /**
     * @return  The GUID used during password reset.
     */
    String getGuid();

    /**
     * The GUID used during password reset.
     *
     * @param   guid The GUID.
     */
    void setGuid(String guid);

    /**
     * @return  True if the users account has expired, false otherwise.
     */
    boolean isExpired();

    /**
     * Sets the expired flag on the User.
     *
     * @param   expired The expired flag.
     */
    void setExpired(boolean expired);

    /**
     * @return  True if the users account is locked, false otherwise.
     */
    boolean isLocked();

    /**
     * Sets the locked flag on the User.
     *
     * @param   locked The locked flag.
     */
    void setLocked(boolean locked);

    /**
     * @return  True if the users password has expired, false otherwise.
     */
    boolean isPasswordExpired();

    /**
     * Sets the password expired flag on the User.
     *
     * @param   passwordExpired The password expired flag.
     */
    void setPasswordExpired(boolean passwordExpired);

    /**
     * @return  True if the account only contains partial information and needs more information from
     *          the user before it can be fully used. These types of accounts are useful when someone
     *          makes a purchase as a guest on the system, but you want to store the transaction
     *          details.
     */
    boolean isPartial();

    /**
     * Sets if the account is a partial account.
     *
     * @param   partial If the account is a partial account.
     */
    void setPartial(boolean partial);

    /**
     * @return  True if the account has been verified. This flag is useful when you want to verify
     *          email addresses or other bits of information before granting a user access.
     */
    boolean isVerified();

    /**
     * Sets if the account is verified or not.
     *
     * @param   verified If the account is verified or not.
     */
    void setVerified(boolean verified);
}