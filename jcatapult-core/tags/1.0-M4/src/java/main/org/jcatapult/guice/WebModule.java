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
package org.jcatapult.guice;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.config.EnvironmentAwareConfiguration;
import org.jcatapult.servlet.ServletObjectsHolder;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * <p>
 * This is the default module that most web applications that use
 * JCatapult will extend. This module provides the ServletContext and
 * the EnvironmentAwareConfiguration to any action or services.
 * </p>
 *
 * <p>
 * This module ensure that it can be configured correctly by verifiying
 * that it is running in a web container. This is done by checking that
 * the {@link ServletObjectsHolder} returns a valid ServletContext instance.
 * If it returns null, that this is a no-op module and it does not bind
 * anything.
 * </p>
 *
 * @author  James Humphrey and Brian Pontarelli
 */
public class WebModule extends AbstractModule {
    /**
     * Calls these methods in this order:
     *
     * <ol>
     * <li>{@link #configureConfiguration()}</li>
     * <li>{@link #configureServletContext()}</li>
     * </ol>
     */
    @Override
    protected void configure() {
        if (ServletObjectsHolder.getServletContext() == null) {
            return;
        }
        
        configureConfiguration();
        configureServletContext();
    }

    /**
     * Configures the servlet context for injection.
     */
    protected void configureServletContext() {
        // Bind the servlet context
        bind(ServletContext.class).toProvider(new Provider<ServletContext>() {
            public ServletContext get() {
                return ServletObjectsHolder.getServletContext();
            }
        }).in(Singleton.class);
    }

    /**
     * Configures the Apache Commons {@link Configuration} for injection.
     */
    protected void configureConfiguration() {
        // Setup the configuration
        bind(Configuration.class).to(EnvironmentAwareConfiguration.class).in(Singleton.class);
    }
}