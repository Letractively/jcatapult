/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.security.EnhancedSecurityContext;

import com.google.inject.Inject;

import org.jcatapult.module.user.service.UserConfiguration;

/**
 * <p>
 * This class is the logout action. This logs the user out of the
 * application and sends a redirect. The URI of the redirect is
 * controlled using a configuration parameter.
 * </p>
 *
 * <h3>Configuration</h3>
 * <p>
 * <strong>inversoft.modules.user.logout.success-uri</strong> -
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