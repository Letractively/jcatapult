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
 */
package org.jcatapult.security.guice;

import org.jcatapult.guice.WorkflowResolverModule;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.login.AuthenticationService;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import org.jcatapult.security.servlet.SecurityWorkflowResolver;
import org.jcatapult.security.spi.EnhancedSecurityContextProvider;
import org.jcatapult.security.spi.SecurityContextProvider;
import org.jcatapult.servlet.WorkflowResolver;

import com.google.inject.Singleton;

/**
 * <p>
 * This class is just a helper class that web applications can extend to
 * ensure that they have defined the correct classes for use with the
 * JCatapult security framework.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class SecurityModule extends WorkflowResolverModule {
    protected void configure() {
        configureWorkflow();
        configureImplementations();
        configureContexts();
    }

    /**
     * Configure the {@link WorkflowResolver} sub-class that the security framework needs.
     */
    protected void configureWorkflow() {
        // Setup the workflow resolver
        bind(WorkflowResolver.class).to(SecurityWorkflowResolver.class).in(Singleton.class);
    }

    /**
     * Configures the implementations of the security frameworks interfaces that the application
     * must provide via the abstract methods of this class.
     */
    protected void configureImplementations() {
        // Set the sub-class security implementations
        bind(AuthenticationService.class).to(getAuthenticationService());
        bind(UserAdapter.class).to(getUserAdapter());
    }

    /**
     * Configure the SecurityContext ({@link SecurityContext} and {@link EnhancedSecurityContext}).
     */
    protected void configureContexts() {
        // Static inject the contexts
        bind(SecurityContextProvider.class).to(JCatapultSecurityContextProvider.class);
        requestStaticInjection(SecurityContext.class);
        bind(EnhancedSecurityContextProvider.class).to(JCatapultSecurityContextProvider.class);
        requestStaticInjection(EnhancedSecurityContext.class);
    }

    /**
     * @return  The authentication service implementation class.
     */
    protected abstract Class<? extends AuthenticationService> getAuthenticationService();

    /**
     * @return  The user adapter implementation class.
     */
    protected abstract Class<? extends UserAdapter> getUserAdapter();
}