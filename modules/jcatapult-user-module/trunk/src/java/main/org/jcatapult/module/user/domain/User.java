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

import java.util.List;
import java.util.Set;

import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.domain.SoftDeletable;

/**
 * <p>
 * This interface defines the contract that is used by the User
 * module. Applications may implement this interface or extend
 * the default implementation that is part of this module.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface User extends Identifiable, SoftDeletable {
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
     * @return  The roles for the user.
     */
    Set<? extends Role> getRoles();

    /**
     * Sets the roles for the user.
     *
     * @param   roles The roles.
     */
    void setRoles(Set<? extends Role> roles);

    /**
     * Adds a role to the user.
     *
     * @param   role The role to add.
     */
    void addRole(Role role);

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
     * @return  The users current password confirmation value from forms.
     */
    String getPasswordConfirm();

    /**
     * Sets the users current password confirmation value from a form.
     *
     * @param   password The password confirmation value.
     */
    void setPasswordConfirm(String password);

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
     * @return  True if the users account is locked, false otherwise.
     */
    boolean isLocked();

    /**
     * @return  True if the users password has expired, false otherwise.
     */
    boolean isPasswordExpired();

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
     * Removes the given credit card from the user.
     *
     * @param   cc The credit card to remove.
     */
    void removeCreditCard(AuditableCreditCard cc);

    /**
     * Adds the given credit card to the user.
     *
     * @param   cc The credit card.
     */
    void addCreditCard(AuditableCreditCard cc);

    /**
     * @return  The list of credit cards for the user.
     */
    List<AuditableCreditCard> getCreditCards();
}