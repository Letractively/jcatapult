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

import javax.servlet.ServletRequest;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This class provides a mechanism for locating user credentials within
 * a Servlet container.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(HttpSessionCredentialStorage.class)
public interface CredentialStorage {

    /**
     * Locates the user credentials.
     *
     * @param   request The HTTP servlet request if it is needed to help find the credentials.
     * @return  The user credentials or null if there are none (i.e. the user is not logged in).
     */
    Object locate(ServletRequest request);

    /**
     * Stores the user credentials into a persistent storage location so that they can be retrieved
     * across multiple requests.
     *
     * @param   credentials The credentials to store.
     * @param   request The HTTP servlet request if it is needed to store the credentials.
     */
    void store(Object credentials, ServletRequest request);

    /**
     * Removes the stored user credentials if there are any.
     *
     * @param   request The HTTP servlet request if it is needed to remove the credentials.
     */
    void remove(ServletRequest request);
}