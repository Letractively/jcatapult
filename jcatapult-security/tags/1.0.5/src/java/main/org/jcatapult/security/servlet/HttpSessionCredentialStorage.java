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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p> This class provides a persistent storage location within the HTTP session for user credentials. This allows
 * applications to locate the user credentials across multiple request. </p> <p/> <p> The object that is stored into the
 * HttpSession is the same object that is returned from the {@link org.jcatapult.security.login.AuthenticationService}
 * and it is stored under the key <code>jcatapult.security.servlet.http.session.credentials</code>. </p>
 *
 * @author Brian Pontarelli
 */
public class HttpSessionCredentialStorage implements CredentialStorage {
  private static final String KEY = "jcatapult.security.servlet.http.session.credentials";

  /**
   * Loads from the session. If there is no session, it is never created.
   *
   * @param   request The request.
   * @return The credentials if they exist, otherwise false.
   */
  public Object locate(ServletRequest request) {
    HttpSession session = ((HttpServletRequest) request).getSession(false);
    if (session != null) {
      return session.getAttribute(KEY);
    }

    return null;
  }

  /**
   * Stores the credentials into the session. If there is no session active, one is created.
   *
   * @param   credentials The credentials to store.
   * @param   request Used to get the HTTP session.
   */
  public void store(Object credentials, ServletRequest request) {
    HttpSession session = ((HttpServletRequest) request).getSession(true);
    session.setAttribute(KEY, credentials);
  }

  /**
   * Removes the credentials from the session.
   *
   * @param   request TO get the session from.
   */
  public void remove(ServletRequest request) {
    HttpSession session = ((HttpServletRequest) request).getSession(false);
    if (session != null) {
      session.removeAttribute(KEY);
    }
  }
}