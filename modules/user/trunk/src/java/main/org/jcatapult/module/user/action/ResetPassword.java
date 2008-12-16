/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action;

import org.jcatapult.mvc.action.annotation.Action;

import com.google.inject.Inject;

import org.jcatapult.module.user.service.UserService;

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
    public String login;

    @Inject
    public ResetPassword(UserService userService) {
        this.userService = userService;
    }

    public String get() {
        return "input";
    }

    public String post() {
        userService.resetPassword(login);
        return "success";
    }
}