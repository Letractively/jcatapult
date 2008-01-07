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

import org.jcatapult.security.spi.SecurityContextProvider;

import com.google.inject.Inject;

/**
 * <p>
 * This class is a ThreadLocal holder that manages an SPI for getting the
 * security credentials.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SecurityContext {
    private static SecurityContextProvider provider;

    /**
     * Returns the current provider.
     *
     * @return The provider.
     */
    public static SecurityContextProvider getProvider() {
        return provider;
    }

    /**
     * Sets a new provider.
     *
     * @param   provider The new provider to use.
     */
    @Inject
    public static void setProvider(SecurityContextProvider provider) {
        SecurityContext.provider = provider;
    }

    /**
     * @return  The currently logged in user's name or some default. If the provider has not been
     *          setup, this method will throw a NullPointerException.
     */
    public static String getCurrentUsername() {
        return provider.getCurrentUsername();
    }

    /**
     * @return  The currently logged in user object.
     */
    public static Object getCurrentUser() {
        return provider.getCurrentUser();
    }

    /**
     * Logs the user into the application.
     *
     * @param   user The user domain object. This is dependent on the provider being used.
     */
    public static void login(Object user) {
        provider.login(user);
    }

    /**
     * Logs the user out of the application.
     */
    public static void logout() {
        provider.logout();
    }

    /**
     * Updates the user that is currently stored in with a new instance.
     *
     * @param   user The new user instance.
     */
    public static void update(Object user) {
        provider.update(user);
    }
}