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

import org.jcatapult.security.spi.EnhancedSecurityContextProvider;

import com.google.inject.Inject;

/**
 * <p> This class is a ThreadLocal holder that manages an SPI for getting the security credentials. </p>
 *
 * @author Brian Pontarelli
 */
public class EnhancedSecurityContext extends SecurityContext {
  protected static EnhancedSecurityContextProvider enhancedProvider;

  /**
   * Sets the enhanced provider and passes it to the SecurityContext.
   *
   * @param provider The enhanced provider.
   */
  @Inject
  public static void setProvider(EnhancedSecurityContextProvider provider) {
    enhancedProvider = provider;
    SecurityContext.setProvider(provider);
  }

  /**
   * Logs the user into the application.
   *
   * @param user The user domain object. This is dependent on the provider being used.
   */
  public static void login(Object user) {
    enhancedProvider.login(user);
  }

  /**
   * Logs the user out of the application.
   */
  public static void logout() {
    enhancedProvider.logout();
  }

  /**
   * Updates the user that is currently stored in with a new instance.
   *
   * @param user The new user instance.
   */
  public static void update(Object user) {
    enhancedProvider.update(user);
  }
}