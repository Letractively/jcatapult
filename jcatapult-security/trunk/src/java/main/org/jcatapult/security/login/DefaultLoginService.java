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

import org.jcatapult.security.PasswordEncryptor;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.UserAdapter;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the default implementation of the login service.
 * It uses the {@link AuthenticationService} to get the user object
 * and the {@link UserAdapter} to get the password to ensure that
 * the passwords match. Since passwords are encrypted during registration
 * using the {@link PasswordEncryptor}, this encrypts the incoming
 * password to compare with the password from the user object
 * retrieved from the AuthenticationService.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultLoginService implements LoginService {
    private final AuthenticationService authenticationService;
    private final UserAdapter userAdapter;
    private final PasswordEncryptor passwordEncryptor;

    @Inject
    public DefaultLoginService(AuthenticationService authenticationService, UserAdapter userAdapter,
            PasswordEncryptor passwordEncryptor) {
        this.authenticationService = authenticationService;
        this.userAdapter = userAdapter;
        this.passwordEncryptor = passwordEncryptor;
    }

    public Object login(String username, String password, Map<String, Object> parameters)
    throws InvalidUsernameException, InvalidPasswordException {
        Object user = authenticationService.loadUser(username, parameters);
        if (user == null) {
            throw new InvalidUsernameException();
        }

        String encrypted = passwordEncryptor.encryptPassword(password, user);
        String userPassword = userAdapter.getPassword(user);
        if (!userPassword.equals(encrypted)) {
            throw new InvalidPasswordException();
        }

        // Save the user to the context since the login was good
        SecurityContext.login(user);

        return user;
    }
}