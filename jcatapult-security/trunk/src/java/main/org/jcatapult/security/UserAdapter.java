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

import java.util.Set;

/**
 * <p>
 * This interface is a user adapter that allows the JCatapult framework access
 * to custom User objects. Rather than force an interface or parent class,
 * this adapter provides applications with the means to implement their
 * User objects however they want. Applications only need to implement this
 * interface to reveal information about the User and register their
 * implementation with JCatapult via Guice.
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
     * Retrieves all of the roles that the user has.
     *
     * @param   user The user.
     * @return  The list of rolls or an empty list if they have none.
     */
    Set<String> getRoles(T user);
}