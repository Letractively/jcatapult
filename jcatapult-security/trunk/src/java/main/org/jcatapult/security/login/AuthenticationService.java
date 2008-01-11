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
package org.jcatapult.security.login;

import java.util.Map;

/**
 * <p>
 * This interface defines the method that JCatapult framework locates users
 * and returns them in a generic fashion. This allows applications to code
 * their own User objects rather than using a parent class or interface
 * to mark them.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface AuthenticationService<T> {
    /**
     * Attempts to load the user from the database or any persistent store. This should not worry about the
     * password at all as that is checked by the code that invokes this interface. If the user account
     * doesn't exist, this should return null.
     *
     * @param   username The username to look for.
     * @param   parameters These are available for implementations that use something other than usernames.
     * @return  The user object or null if it doesn't exist.
     */
    T loadUser(String username, Map<String, Object> parameters);
}