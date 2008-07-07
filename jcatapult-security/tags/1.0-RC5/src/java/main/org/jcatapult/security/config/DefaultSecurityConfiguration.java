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

import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This class implements the security configuration interface.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultSecurityConfiguration implements SecurityConfiguration {
    private final Configuration configuration;

    @Inject
    public DefaultSecurityConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    public String getSalt() {
        return configuration.getString("jcatapult.security.salt", "jcatapult");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBase64Encoded() {
        return configuration.getBoolean("jcatapult.security.password-encryptor.encode-base-64", true);
    }

    /**
     * {@inheritDoc}
     */
    public String getAlgorithm() {
        return configuration.getString("jcatapult.security.password-encryptor.algorithm", "MD5");
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthorizationRules() {
        return configuration.getString("jcatapult.security.authorization.rules", "/admin**=admin");
    }

    /**
     * {@inheritDoc}
     */
    public String getRestrictedURI() {
        return configuration.getString("jcatapult.security.authorization.restricted-uri", "/not-authorized");
    }

    /**
     * {@inheritDoc}
     */
    public String getNotLoggedInURI() {
        return configuration.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in");
    }

    /**
     * {@inheritDoc}
     */
    public String getLoginURI() {
        return configuration.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check");
    }

    /**
     * {@inheritDoc}
     */
    public String getLoginSuccessURI() {
        return configuration.getString("jcatapult.security.login.success-uri", "/login-success");
    }

    /**
     * {@inheritDoc}
     */
    public String getLoginFailedURI() {
        return configuration.getString("jcatapult.security.login.failed-uri", "/login-failed");
    }

    /**
     * {@inheritDoc}
     */
    public String getUsernameParameter() {
        return configuration.getString("jcatapult.security.login.username-parameter", "j_username");
    }

    /**
     * {@inheritDoc}
     */
    public String getPasswordParameter() {
        return configuration.getString("jcatapult.security.login.password-parameter", "j_password");
    }
}