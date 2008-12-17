/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;

import com.google.inject.Inject;

import org.jcatapult.module.user.service.UserConfiguration;

/**
 * <p>
 * This class handles the when a user is not logged in. It redirects
 * the to the login page.
 * </p>
 *
 * <h3>Configuration</h3>
 * <p>
 * <strong>inversoft.modules.user.login.uri</strong> -
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