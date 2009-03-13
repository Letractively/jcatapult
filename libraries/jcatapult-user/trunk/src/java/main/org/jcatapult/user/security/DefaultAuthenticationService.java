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
package org.jcatapult.user.security;

import java.util.Map;

import org.jcatapult.security.login.AuthenticationService;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserService;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the JCatapult security framework to fetch Jcatapult user objects
 * from the database.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultAuthenticationService implements AuthenticationService<User> {
    private final UserService userService;

    @Inject
    public DefaultAuthenticationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Locates the user. If the user is partial, this always returns null.
     *
     * @param   username The username.
     * @param   parameters Any additional parameters (not used here).
     * @return  The user.
     */
    public User loadUser(String username, Map<String, Object> parameters) {
        User user = userService.findByLogin(username);
        if (user != null && user.isPartial()) {
            return null;
        }

        return user;
    }
}