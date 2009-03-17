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
package org.jcatapult.module.simpleuser.action;

import javax.servlet.http.HttpServletRequest;

import org.jcatapult.module.simpleuser.util.URLTools;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserService;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles sending of the password reset user accounts.
 * Currently, if an account for the login doesn't exist, this does
 * not error out. In the next release this will leverage a captcha
 * image in order to prevent hacking of user account information.
 * However, until then, we can't inform the user if an account
 * doesn't exist otherwise they will have at least part of the
 * account information and could brutal force crack the password.
 * </p>
 *
 * <h3>Success</h3>
 * <p>
 * If this action completes successfully, this renders the
 * reset-password-success.ftl or reset-password.ftl file, in that
 * order.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
public class ResetPassword {
    private final UserService userService;
    private final HttpServletRequest request;
    public String login;

    @Inject
    public ResetPassword(UserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    public String get() {
        return "input";
    }

    public String post() {
        User user = userService.findByUsernameOrEmail(login);
        if (user != null) {
            String url = URLTools.makeURL(request, "/change-password");
            userService.resetPassword(user.getId(), url);
        }
        
        return "success";
    }
}