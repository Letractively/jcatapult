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
package org.jcatapult.module.user.security;

import java.util.HashSet;
import java.util.Set;

import org.jcatapult.security.UserAdapter;

import org.jcatapult.module.user.domain.Role;
import org.jcatapult.module.user.domain.User;

/**
 * <p>
 * This class is the Jcatapult users details service that fetches users
 * information from the database.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultUserAdapter<T extends User> implements UserAdapter<T> {
    public String getUsername(T user) {
        return user.getLogin();
    }

    public String getPassword(T user) {
        return user.getPassword();
    }

    public boolean isExpired(T user) {
        return user.isExpired();
    }

    public boolean isLocked(T user) {
        return user.isLocked();
    }

    public boolean areCredentialsExpired(T user) {
        return user.isPasswordExpired();
    }

    public Set<String> getRoles(T user) {
        Set<String> roles = new HashSet<String>();
        for (Role role : user.getRoles()) {
            roles.add(role.getName());
        }

        return roles;
    }
}