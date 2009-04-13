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
 *
 */
package org.jcatapult.module.simpleuser.service;

import java.util.HashMap;
import java.util.Map;

import org.jcatapult.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This class implements the configuration service and loads up the default
 * configuration flags that control the forms and validation logic for the
 * default user and role Classes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultUserConfiguration implements UserConfiguration {

    /* Registration configureation */
    public static final String REGISTRATION_DISABLED = "jcatapult.user.register.disabled";
    public static final String VERIFY_EMAILS = "jcatapult.user.verify-emails";
    public static final String REGISTRATION_SUCCESS_URI = "jcatapult.user.register.success-uri";
    public static final String LOGOUT_SUCCESS_URI = "jcatapult.user.logout.success-uri";
    public static final String LOGIN_SUCCESS_URI = "jcatapult.user.login.success-uri";
    public static final String LOGIN_URI = "jcatapult.user.login.uri";

    private final Map<String, Boolean> flags = new HashMap<String, Boolean>();
    private final String registrationSuccessURI;
    private final String logoutURI;
    private final String loginURI;
    private final String loginSuccessURI;

    @Inject
    public DefaultUserConfiguration(Configuration configuration) {
        // Registration
        flags.put(REGISTRATION_DISABLED, configuration.getBoolean(REGISTRATION_DISABLED, false));
        flags.put(VERIFY_EMAILS, configuration.getBoolean(VERIFY_EMAILS, false));
        this.registrationSuccessURI = configuration.getString(REGISTRATION_SUCCESS_URI, "/");

        // Logout
        this.logoutURI = configuration.getString(LOGOUT_SUCCESS_URI, "/");

        // Login
        this.loginURI = configuration.getString(LOGIN_URI, "/login");
        this.loginSuccessURI = configuration.getString(LOGIN_SUCCESS_URI, "/");
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> getDomainFlags() {
        return flags;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRegistrationDisabled() {
        return flags.get(REGISTRATION_DISABLED);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVerifyEmails() {
        return flags.get(VERIFY_EMAILS);
    }

    /**
     * {@inheritDoc}
     */
    public String getRegistrationSuccessURI() {
        return registrationSuccessURI;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogoutURI() {
        return logoutURI;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogingURI() {
        return loginURI;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogingSuccessURI() {
        return loginSuccessURI;
    }
}