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
package org.jcatapult.servlet;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jcatapult.guice.ConfigurationModule;
import org.jcatapult.jpa.EntityManagerContext;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.UserCGLIBCallback;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

/**
 * <p>
 * This is the Servlet filter for the JCatapult framework that allows any
 * User object to be placed into the HTTP request Object for each request.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JCatapultUserFilter implements Filter {
    private boolean proxyUser;
    private String userClass;
    private Factory factory;

    /**
     * This boolean configuration comes from the JCatapult configuration property named
     * <strong>jcatapult.user.filter.proxy.user</strong>. This determines if the user object should be
     * proxied using the CGLIB library so that it can be dynamically re-attached to a JPA EntityManager
     * for lazy loading.
     *
     * @param    proxyUser The flag that controls user proxying.
     */
    @Inject(optional = true)
    public void setProxyUser(@Named("jcatapult.user.filter.proxy.user") boolean proxyUser) {
        this.proxyUser = proxyUser;
    }

    /**
     * If user proxying is turned on, this property must be set so that the CGLIB enhancement can
     * be optimized ahead of time rather than per-request. This property is controled by the JCatapult
     * configuration property <strong>jcatapult.user.filter.user.class</strong> and should reference
     * a full class name (including the package).
     *
     * @param   userClass The user class to proxy.
     */
    @Inject(optional = true)
    public void setUserClass(@Named("jcatapult.user.filter.user.class") String userClass) {
        this.userClass = userClass;
    }

    /**
     * Does nothing.
     *
     * @param   filterConfig The filter config.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inject the JCatapult configuration
        Injector injector = Guice.createInjector(new ConfigurationModule());
        injector.injectMembers(this);

        if (proxyUser) {
            Class<?> userClass;
            try {
                userClass = Class.forName(this.userClass);
            } catch (ClassNotFoundException e) {
                throw new ServletException("Invalid user class [" + this.userClass +
                    "] defined for CGLIB proxying by the JCatapultUserFilter.");
            }

            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(userClass);
            enhancer.setCallback(new UserCGLIBCallback(null, null));
            this.factory = (Factory) enhancer.create();
        }
    }

    /**
     * Grabs the user, proxies it if that configuration is setup, and then stores it into the request
     * for access within the web application.
     *
     * @param   request Passed down chain.
     * @param   response Passed down chain.
     * @param   chain The chain.
     * @throws  java.io.IOException If the chain throws an exception.
     * @throws  javax.servlet.ServletException If the chain throws an exception.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
        // Setup the user object into the session, if we can
        if (SecurityContext.getProvider() != null) {
            Object user = SecurityContext.getCurrentUser();
            if (proxyUser) {
                EntityManager entityManager = EntityManagerContext.get();
                user = factory.newInstance(new UserCGLIBCallback(user, entityManager));
            }

            request.setAttribute("user", user);
        }

        // Proceed down the chain
        chain.doFilter(request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }
}