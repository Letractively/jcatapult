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

import org.jcatapult.security.JCatapultSecurityException;
import org.jcatapult.security.login.AuthenticationService;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserService;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the JCatapult security framework to fetch Jcatapult user objects
 * from the database. This checks all of the security constraints on the {@link User}
 * and if any of the constraints fails, it throws a JCatapultSecurityException
 * with the given Strings:
 * </p>
 *
 * <ul>
 * <li>not-verified - The account hasn't been verified</li>
 * <li>expired - The account has expired</li>
 * <li>locked - The account is locked</li>
 * <li>password-expired - The account's password has expired</li>
 * </ul>
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
        if (user != null && user.isPartial() || user == null) {
            return null;
        }

        if (!user.isVerified()) {
            throw new JCatapultSecurityException("not-verified");
        } else if (user.isExpired()) {
            throw new JCatapultSecurityException("expired");
        } else if (user.isLocked()) {
            throw new JCatapultSecurityException("locked");
        } else if (user.isPasswordExpired()) {
            throw new JCatapultSecurityException("password-expired");
        }

        return user;
    }
}