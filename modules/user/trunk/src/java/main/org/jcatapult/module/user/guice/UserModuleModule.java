/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.guice;

import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.guice.SecurityModule;
import org.jcatapult.security.login.AuthenticationService;

import com.inversoft.module.user.security.DefaultAuthenticationService;
import com.inversoft.module.user.security.DefaultUserAdapter;

/**
 * <p>
 * This class extends the JCatapult security module and sets the
 * services that are required for that functionality.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class UserModuleModule extends SecurityModule {

    @Override
    protected void configure() {
        super.configure();
    }

    protected Class<? extends AuthenticationService> getAuthenticationService() {
        return DefaultAuthenticationService.class;
    }

    protected Class<? extends UserAdapter> getUserAdapter() {
        return DefaultUserAdapter.class;
    }
}