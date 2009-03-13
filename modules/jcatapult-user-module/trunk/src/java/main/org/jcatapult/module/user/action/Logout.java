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
import org.jcatapult.security.EnhancedSecurityContext;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the logout action. This logs the user out of the
 * application and sends a redirect. The URI of the redirect is
 * controlled using a configuration parameter.
 * </p>
 *
 * <h3>Configuration</h3>
 * <p>
 * <strong>jcatapult.user.logout.success-uri</strong> -
 * This is a String configuration element that contains the URI that
 * the user is redirected to after a successful logout. Defaults to
 * <strong>/</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "${uri}")
public class Logout {
    private final UserConfiguration userConfiguration;
    private String uri;

    @Inject
    public Logout(UserConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Logs the user out.
     *
     * @return  Always success.
     */
    public String execute() {
        uri = userConfiguration.getLogoutURI();
        EnhancedSecurityContext.logout();
        return "success";
    }
}