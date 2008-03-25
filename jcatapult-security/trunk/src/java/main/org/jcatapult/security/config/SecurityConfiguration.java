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
package org.jcatapult.security.config;

/**
 * <p>
 * This interface defines the configuration parameters for the JCatapult Security
 * system and the defaults for all of them. This centralizes the location of all
 * of the configuration and removes all of the constants in other code.
 * </p>
 *
 * <h3>Password Configuration</h3>
 * <dl>
 * <dt>jcatapult.security.salt</dt>
 * <dd>A String that defines the salt source for password encryption. Defaults
 *  to <code>jcatapult</code></dd>
 * <dt>jcatapult.security.password-encryptor.encode-base-64</dt>
 * <dd>A boolean that determines if the password encryptor uses base 64 encoding.
 *  Defaults to <code>true</code></dd>
 * <dt>jcatapult.security.password-encryptor.algorithm</dt>
 * <dd>A String that sets the algorithm used for password encryption. Defaults
 *  to <code>MD5</code></dd>
 * </dl>
 *
 * <h3>Authorization Configuration</h3>
 * <dl>
 * <dt>jcatapult.security.authorization.rules</dt>
 * <dd>This is a text entry that configures the authorization rules. Each rule is
 *  placed on a separate line and all white space before and after the rule is stripped.
 *  Each rule begins with a URI definition that uses Apache Ant wildcard syntax,
 *  followed by an equal sign and finally a comma separated list of required roles
 *  necessary to access the resource. For more information consult the JavaDoc for
 *  the {@link org.jcatapult.security.auth.ConfiguredAuthorizer} class.
 *  Defaults to <code>/admin**=admin</code></dd>
 * <dt>jcatapult.security.authorization.restricted-uri</dt>
 * <dd>A String that sets the URI where the request is forwarded to if the user
 *  requests a resource and they don't have the correct roles to access it. Defaults
 *  to <code>/not-authorized</code></dd>
 * <dt>jcatapult.security.authorization.not-logged-in-uri</dt>
 * <dd>A String that sets the URI where the request is forwarded to if the user
 *  requests a protected resource and they are not logged in. Defaults to
 *  <code>/not-logged-in</code></dd>
 * </dl>
 *
 * <h3>Login Configuration</h3>
 * <dl>
 * <dt>jcatapult.security.login.submit-uri</dt>
 * <dd>A String that sets the URI the JCatapult Security system watches for to
 *  determine if the user is logging in. Defaults to <code>/jcatapult-security-check</code></dd>
 * <dt>jcatapult.security.login.success-uri</dt>
 * <dd>A String that sets the URI where the request is forwarded to if the user's
 *  attempt to login is successful. Defaults to <code>/login-success</code></dd>
 * <dt>jcatapult.security.login.failed-uri</dt>
 * <dd>A String that sets the URI where the request is forwarded to if the user's
 *  attempt to login fails. Defaults to <code>/login-failed</code></dd>
 * <dt>jcatapult.security.login.username-parameter</dt>
 * <dd>A String that sets the HTTP request parameter that stores the username during
 *  login. Defaults to <code>j_username</code></dd>
 * <dt>jcatapult.security.login.password-parameter</dt>
 * <dd>A String that sets the HTTP request parameter that stores the password during
 *  login. Defaults to <code>j_password</code></dd>
 * </dl>
 *
 * @author  Brian Pontarelli
 */
public interface SecurityConfiguration {
    /**
     * @return  The password encryption salt.
     */
    String getSalt();

    /**
     * @return  If the password encryption should be base 64 encoded or not.
     */
    boolean isBase64Encoded();

    /**
     * @return  The password encryption algorithm.
     */
    String getAlgorithm();

    /**
     * @return  The authorization rules text.
     */
    String getAuthorizationRules();

    /**
     * @return  The restricted URI.
     */
    String getRestrictedURI();

    /**
     * @return  The not logged in URI.
     */
    String getNotLoggedInURI();

    /**
     * @return  The login submit URI.
     */
    String getLoginURI();

    /**
     * @return  The successful login URI.
     */
    String getLoginSuccessURI();

    /**
     * @return  The failed login URI.
     */
    String getLoginFailedURI();

    /**
     * @return  The username parameter
     */
    String getUsernameParameter();

    /**
     * @return  The password parameter
     */
    String getPasswordParameter();
}