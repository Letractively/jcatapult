/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.module.simpleuser.action;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.module.simpleuser.util.URLTools;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.action.result.annotation.Redirects;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.scope.annotation.Flash;
import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserService;
import org.jcatapult.security.EnhancedSecurityContext;

import com.google.inject.Inject;

/**
 * <p>
 * This class is part of the password reset process. The application
 * collects the users email address and then emails them a link to
 * reset their password. The link in the email must point to this
 * action. This action loads the users account based on a large GUID.
 * Once the users account is loaded, it is stored into the session
 * until the user has successfully changed their password.
 * </p>
 * <p>
 * There is only a single failure case for this action, if the users
 * GUID has been reset and they have a bad link or if the user is trying
 * to hack into other peoples accounts using the GUID (unlikely though
 * since it is a secure random and 32 characters of HEX). When this
 * failure occurs the user is forwarded to the change-password.ftl
 * file and an error is displayed. That error is controlled using
 * a configuration parameter.
 * </p>
 *
 * <h3>Localization</h3>
 * <dl>
 * <dt>missing</dt>
 * <dd>The message displayed to users if the user for the given GUID
 * cannot be found in order to complete the password reset.
 * </dd>
 * </dl>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirects({
    @Redirect(uri = "email-verified"),
    @Redirect(code = "success-post", uri = "verification-email-sent")
})
public class VerifyEmail {
    private final UserService userService;
    private final HttpServletRequest request;
    public final MessageStore messageStore;

    // The user
    @Flash
    public User verifyEmailUser;

    // For get
    public String guid;

    // For post
    @Required
    public String email;

    @Inject
    public VerifyEmail(MessageStore messageStore, UserService userService, HttpServletRequest request) {
        this.messageStore = messageStore;
        this.userService = userService;
        this.request = request;
    }

    public String get() {
        if (guid == null) {
            return "input";
        }

        verifyEmailUser = userService.findByGUID(guid);
        if (verifyEmailUser == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing");
            return "error";
        }
        
        verifyEmailUser.setVerified(true);
        verifyEmailUser.setGuid(null);
        if (!userService.persist(verifyEmailUser)) {
            messageStore.addActionError(MessageScope.REQUEST, "error");
            return "error";
        }

        EnhancedSecurityContext.login(verifyEmailUser);

        return "success";
    }

    public String post() {
        String url = URLTools.makeURL(request, "verify-email");
        try {
            userService.resendVerificationEmail(email, url);
        } catch (EntityNotFoundException e) {
            // Smother
        }

        return "success-post";
    }
}