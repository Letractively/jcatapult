/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Forward;

/**
 * <p>
 * This class handles a failed login. This takes the user back to
 * the login form and displays the errors.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Forward(page = "login.ftl")
public class LoginFailed {
    public String j_username;
    public String j_password;

    public String execute() {
        return "success";
    }
}