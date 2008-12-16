/*
 * Copyright (c) 2001-2007, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.security;

import java.util.Map;

import org.jcatapult.security.login.AuthenticationService;

import com.google.inject.Inject;

import com.inversoft.module.user.domain.User;
import com.inversoft.module.user.service.UserService;

/**
 * <p>
 * This class is the JCatapult security framework to fetch Inversoft user objects
 * from the database.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultAuthenticationService implements AuthenticationService<User> {
    private final UserService userService;

    @Inject
    public DefaultAuthenticationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Locates the user. If the user is partial, this always returns null.
     *
     * @param   username The username.
     * @param   parameters Any additional parameters (not used here).
     * @return  The user.
     */
    public User loadUser(String username, Map<String, Object> parameters) {
        User user = userService.findByLogin(username);
        if (user != null && user.isPartial()) {
            return null;
        }

        return user;
    }
}