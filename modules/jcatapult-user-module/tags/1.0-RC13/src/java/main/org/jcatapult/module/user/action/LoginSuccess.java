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
package org.jcatapult.module.user.action;

import org.jcatapult.module.user.service.UserConfiguration;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles the login form, failed login and successful login.
 * </p>
 *
 * <h2>Successful login</h2>
 * <p>
 * If the login is successful and there is a saved request, than it is
 * handled by the JCatapult security framework. This action only handles
 * the case when the user logins in using the login button or link. After
 * a successful login, this action is called by the JCatapult security
 * framework and it performs a redirect to a URI. That URI is controlled
 * by a configuration parameter.
 * </p>
 *
 * <h3>Success</h3>
 * <p>
 * If the action completes successfully, it renders the URI setup
 * by the configuration parameter.
 * </p>
 *
 * <h3>Configuration</h3>
 * <p>
 * <strong>jcatapult.user.login.success-uri</strong> -
 * This is a String configuration element that contains the URI that
 * the user is redirected to after a successful login. Defaults to
 * <strong>/</strong>.
 * <strong>jcatapult.user.login.uri</strong> -
 * This is a String configuration element that contains the URI that
 * the user is redirected to in order to login. Defaults to
 * <strong>/login</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "${uri}")
public class LoginSuccess {
    private final UserConfiguration userConfiguration;
    public String uri;
    public String j_username;
    public String j_password;

    @Inject
    public LoginSuccess(UserConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    public String execute() {
        uri = userConfiguration.getLogingSuccessURI();
        return "success";
    }
}