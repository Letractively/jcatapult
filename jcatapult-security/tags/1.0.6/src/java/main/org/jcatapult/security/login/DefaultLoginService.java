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

import org.jcatapult.security.JCatapultSecurityException;
import org.jcatapult.security.PasswordEncryptor;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;

import com.google.inject.Inject;

/**
 * <p> This class is the default implementation of the login service. It uses the {@link AuthenticationService} to get
 * the user object and the {@link UserAdapter} to get the password to ensure that the passwords match. Since passwords
 * are encrypted during registration using the {@link PasswordEncryptor}, this encrypts the incoming password to compare
 * with the password from the user object retrieved from the AuthenticationService. </p> <p/> <p> In order to track
 * logins (both successful and failed), you can optionally register an {@link AuthenticationListener} with this service.
 * That listener will be called whenever a login fails or succeeds. </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultLoginService implements LoginService {
  private final AuthenticationService authenticationService;
  private final UserAdapter userAdapter;
  private final PasswordEncryptor passwordEncryptor;
  private final JCatapultSecurityContextProvider securityContextProvider;
  private AuthenticationListener authenticationListener;

  @Inject
  public DefaultLoginService(AuthenticationService authenticationService, UserAdapter userAdapter,
                             PasswordEncryptor passwordEncryptor, JCatapultSecurityContextProvider securityContextProvider) {
    this.authenticationService = authenticationService;
    this.userAdapter = userAdapter;
    this.passwordEncryptor = passwordEncryptor;
    this.securityContextProvider = securityContextProvider;
  }

  /**
   * Sets in the authentication listener for this application.
   *
   * @param   listener The listener.
   */
  @Inject(optional = true)
  public void setAuthenticationListener(AuthenticationListener listener) {
    this.authenticationListener = listener;
  }

  @SuppressWarnings("unchecked")
  public Object login(String username, String password, Map<String, Object> parameters)
    throws InvalidUsernameException, InvalidPasswordException {
    Object user;
    try {
      user = authenticationService.loadUser(username, parameters);
      if (user == null) {
        throw new InvalidUsernameException();
      }

      String encrypted = passwordEncryptor.encryptPassword(password, user);
      String userPassword = userAdapter.getPassword(user);
      if (!userPassword.equals(encrypted)) {
        throw new InvalidPasswordException();
      }
    } catch (JCatapultSecurityException e) {
      if (authenticationListener != null) {
        authenticationListener.failedLogin(username, password, parameters);
      }

      throw e;
    }

    // Save the user to the context since the login was good
    if (authenticationListener != null) {
      authenticationListener.successfulLogin(user);
    }
    securityContextProvider.login(user);

    return user;
  }
}