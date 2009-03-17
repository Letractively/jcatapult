/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
 * This interface defines a mechanism by which applications can listener for
 * authentication events. There can only be one listener per application.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface AuthenticationListener<T> {
    /**
     * The given user successfully logged in.
     *
     * @param   user The user.
     */
    void successfulLogin(T user);

    /**
     * The given user failed to logged in.
     *
     * @param   username The username.
     * @param   password The password.
     * @param   parameters The parameters.
     */
    void failedLogin(String username, String password, Map<String, Object> parameters);
}
