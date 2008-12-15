/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.scope.annotation.Flash;
import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.mvc.validation.annotation.ValidateMethod;
import org.jcatapult.security.EnhancedSecurityContext;

import com.google.inject.Inject;

import com.inversoft.module.user.domain.User;
import com.inversoft.module.user.service.UpdateResult;
import com.inversoft.module.user.service.UserService;

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
@Redirect(uri = "/password-updated")
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

        UpdateResult result = userService.updatePassword(passwordResetUser.getLogin(), password);
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