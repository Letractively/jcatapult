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
package org.jcatapult.security;

/**
 * <p>
 * This interface is a user adapter that allows the JCatapult framework access
 * to custom User objects. Rather than force an interface or parent class,
 * this adapter provides applications with the means to implement their
 * User objects however they want.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface UserAdapter<T> {
    /**
     * Retrieves the username for the given User object.
     *
     * @param   user The user object.
     * @return  The username.
     */
    String getUsername(T user);

    /**
     * Retrieves the password for the given User object.
     *
     * @param   user The user object.
     * @return  The password so that it can be verified.
     */
    String getPassword(T user);

    /**
     * Determines if the users account has expired (trial period or something like that).
     *
     * @param   user The user to verify.
     * @return  True if the account is expired, false otherwise.
     */
    boolean isExpired(T user);

    /**
     * Determines if the users account has been locked (by an admin or someone with great power - hehe).
     *
     * @param   user The user to verify.
     * @return  True if the account is locked, false otherwise.
     */
    boolean isLocked(T user);

    /**
     * Determines if the users account has been disabled (by an admin or by the user).
     *
     * @param   user The user to verify.
     * @return  True if the account is disabled, false otherwise.
     */
    boolean isDisabled(T user);

    /**
     * Determines if any of the users credenditals have expired (the password was reset and needs
     * to be changed or the password is old).
     *
     * @param   user The user to verify.
     * @return  True if any credentials have expired, false otherwise.
     */
    boolean areCredentialsExpired(T user);
}