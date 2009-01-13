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

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This service defines the mechanism that logs users into the system. This
 * is an abstraction that is used in conjunction with the {@link AuthenticationService}
 * and verifies the password is correct and throws appropriate exceptions
 * on failures.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultLoginService.class)
public interface LoginService {
    /**
     * Attempts to log the user into the application.
     *
     * @param   username The username from a form.
     * @param   password The password from a form.
     * @param   parameters Any extra parameters that might be used to log the user in.
     * @return  The user object if it was found.
     * @throws InvalidUsernameException If the username is incorrect.
     * @throws  InvalidPasswordException If the password is incorrect.
     */
    Object login(String username, String password, Map<String, Object> parameters)
    throws InvalidUsernameException, InvalidPasswordException;
}