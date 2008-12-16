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
 * This class handles the login form, failed login and successful login.
 * </p>
 *
 * <h2>Successful login</h2>
 * <p>
 * If the login is successful and there is a saved request, than it is
 * handled by the JCatapult security framework. This action only handles
 * the case when the user logins in using the login button or link. After
 * a successful login, this action is called by the JCatapult security
 * framework and it performs a redirect to a URI. That URI is controlled
 * by a configuration parameter.
 * </p>
 *
 * <h3>Success</h3>
 * <p>
 * If the action completes successfully, it renders the URI setup
 * by the configuration parameter.
 * </p>
 *
 * <h3>Configuration</h3>
 * <p>
 * <strong>inversoft.modules.user.login.success-uri</strong> -
 * This is a String configuration element that contains the URI that
 * the user is redirected to after a successful login. Defaults to
 * <strong>/</strong>.
 * <strong>inversoft.modules.user.login.uri</strong> -
 * This is a String configuration element that contains the URI that
 * the user is redirected to in order to login. Defaults to
 * <strong>/login</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "${uri}")
public class LoginSuccess {
    private final UserConfiguration userConfiguration;
    public String uri;
    public String j_username;
    public String j_password;

    @Inject
    public LoginSuccess(UserConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    public String execute() {
        uri = userConfiguration.getLogingSuccessURI();
        return "success";
    }
}