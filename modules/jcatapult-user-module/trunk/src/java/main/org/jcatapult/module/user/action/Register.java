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

import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.saved.SavedRequestService;
import org.jcatapult.user.service.RegisterResult;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the main registration action. It provides the functionality
 * of a new user to the website registering with the site. There are a number
 * of options that can be used to control the regsitration for a site.
 * </p>
 *
 * <h3>Errors</h3>
 * <p>
 * There are a number of error cases for this action. First, if the
 * validation fails, this renders the register-input.ftl or register.ftl
 * file, in that order.
 * </p>
 * <p>
 * Second, if the registration is disabled, this
 * </p>
 *
 * <h3>Success</h3>
 * <p>
 * </p>
 *
 * <h3>Submit Configuration</h3>
 * <p>
 * <strong>jcatapult.user.register.disabled</strong> - This is
 * a boolean configuration element that can be used to disable user registration
 * for any aplication. If registration is disabled and the user is attempting
 * to submit a registration (hacking or some old form), this will forward the
 * request to register.jsp. Defaults to <strong>false</strong>.
 * </p>
 * <p>
 * <strong>jcatapult.user.register.disabled-message-key</strong> -
 * This is a String configuration element that contains the key used to
 * fetch the message displayed to users from the Struts2 resource bundle
 * if the registration is disabled. Defaults to <strong>disabled</strong>.
 * </p>
 * <p>
 * <strong>jcatapult.user.register.exists-message-key</strong> -
 * This is a String configuration element that contains the key used to
 * fetch the message displayed to users from the Struts2 resource bundle
 * if the username has already been registered. Defaults to <strong>exists</strong>.
 * </p>
 * <p>
 * <strong>jcatapult.user.register.error-message-key</strong> -
 * This is a String configuration element that contains the key used to
 * fetch the message displayed to users from the Struts2 resource bundle
 * if the registration failed due to an unknown error. Defaults to
 * <strong>error</strong>.
 * </p>
 * <p>
 * <strong>jcatapult.user.register.success-uri</strong> -
 * This is a String configuration element that contains the URI that the user
 * is redirected to after a successful registration. If there is a saved
 * request (i.e. the user clicked a link or submitted a form that required
 * login), this action will use the saved request and redirect to that.
 * Defaults to <strong>/</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "${uri}")
public class Register extends BaseUserFormAction {
    private final SavedRequestService savedRequestService;
    private final HttpServletRequest request;
    private String uri;

    @Inject
    public Register(SavedRequestService savedRequestService, HttpServletRequest request) {
        this.savedRequestService = savedRequestService;
        this.request = request;
    }

    public String getUri() {
        return uri;
    }

    @ActionPrepareMethod
    public void prepare() {
        user = userService.createUser();
    }

    public String get() {
        if (userConfiguration.isRegistrationDisabled()) {
            messageStore.addActionError(MessageScope.REQUEST, "disabled");
            return "disabled";
        }

        return "input";
    }

    public String post() {
        if (userConfiguration.isRegistrationDisabled()) {
            messageStore.addActionError(MessageScope.REQUEST, "disabled");
            return "disabled";
        }

        RegisterResult result = userService.register(user, password);
        if (result == RegisterResult.EXISTS) {
            messageStore.addFieldError(MessageScope.REQUEST, "user.login", "user.login.exists");
            return "input";
        } else if (result == RegisterResult.ERROR) {
            messageStore.addActionError(MessageScope.REQUEST, "error");
            return "error";
        }

        // Login the user in
        EnhancedSecurityContext.login(user);

        // See if there is a saved request
        uri = savedRequestService.processSavedRequest(request);
        if (uri == null) {
            uri = userConfiguration.getRegistrationSuccessURI();
        }

        return "success";
    }
}