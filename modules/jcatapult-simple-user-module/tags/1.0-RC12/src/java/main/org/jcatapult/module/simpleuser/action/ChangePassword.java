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

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.scope.annotation.Flash;
import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.mvc.validation.annotation.ValidateMethod;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UpdateResult;
import org.jcatapult.user.service.UserService;

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
@Redirect(uri = "password-updated")
public class ChangePassword {
    private final UserService userService;
    public final MessageStore messageStore;

    // The user
    @Flash
    public User passwordResetUser;

    // For get
    public String guid;

    // For post
    @Required
    public String password;
    public String passwordConfirm;

    @Inject
    public ChangePassword(MessageStore messageStore, UserService userService) {
        this.messageStore = messageStore;
        this.userService = userService;
    }

    public String get() {
        if (guid == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing");
        } else {
            passwordResetUser = userService.findByGUID(guid);
            if (passwordResetUser == null) {
                messageStore.addActionError(MessageScope.REQUEST, "missing");
            }
        }

        return "input";
    }

    public String post() throws Exception {
        if (passwordResetUser == null) {
            messageStore.addActionError(MessageScope.REQUEST, "error");
            return "input";
        }

        UpdateResult result = userService.updatePassword(passwordResetUser.getId(), password);
        if (result == UpdateResult.ERROR || result == UpdateResult.MISSING) {
            messageStore.addActionError(MessageScope.REQUEST, "error");
            return "input";
        }

        // Log the user in and clear the flash
        EnhancedSecurityContext.login(passwordResetUser);
        passwordResetUser = null;
        return "success";
    }

    @ValidateMethod
    public void validate() {
        if (password != null && (passwordConfirm == null || !password.equals(passwordConfirm))) {
            messageStore.addFieldError(MessageScope.REQUEST, "passwordConfirm", "passwordConfirm.match");
        }
    }
}