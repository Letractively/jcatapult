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
package org.jcatapult.security.servlet;

import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.spi.EnhancedSecurityContextProvider;
import org.jcatapult.servlet.ServletObjectsHolder;

import com.google.inject.Inject;

/**
 * <p>
 * This class implements the SecurityContextProvider using the JCatapult security
 * framework. Mainly, this uses the implementations of the {@link UserAdapter}
 * and {@link CredentialStorage} interfaces in order to fulfill the methods. Since
 * the {@link CredentialStorage} is an HTTP servlet specific interface, this uses
 * the {@link ServletObjectsHolder} thread local storage that is used in the
 * {@link org.jcatapult.servlet.JCatapultFilter} in order to get access to the HTTP
 * classes that are required. This is somewhat of a hack, but it is very difficult
 * </p>
 *
 * @author Brian Pontarelli
 */
public class JCatapultSecurityContextProvider implements EnhancedSecurityContextProvider {
    private static final ThreadLocal<Object> userHolder = new ThreadLocal<Object>();
    private final UserAdapter userAdapter;

    @Inject
    public JCatapultSecurityContextProvider(UserAdapter userAdapter) {
        this.userAdapter = userAdapter;
    }

    public String getCurrentUsername() {
        Object user = getCurrentUser();
        if (user != null) {
            return userAdapter.getUsername(user);
        }

        return "anonymous";
    }

    public void login(Object user) {
        userHolder.set(user);
    }

    public void logout() {
        userHolder.remove();
    }

    public Object getCurrentUser() {
        return userHolder.get();
    }

    public void update(Object user) {
        login(user);
    }
}