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
 * This class handles the when a user is not logged in. It redirects
 * the to the login page.
 * </p>
 *
 * <h3>Configuration</h3>
 * <p>
 * <strong>jcatapult.user.login.uri</strong> -
 * This is a String configuration element that contains the URI that
 * the user is redirected to in order to login. Defaults to
 * <strong>/login</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "${loginURI}")
public class NotLoggedIn {
    private final UserConfiguration userConfiguration;
    private String loginURI;

    @Inject
    public NotLoggedIn(UserConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    public String getLoginURI() {
        return loginURI;
    }

    public String execute() {
        loginURI = userConfiguration.getLogingURI();
        return "success";
    }
}