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
package org.jcatapult.mvc.action.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletContext;

import org.jcatapult.mvc.action.annotation.Action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.java.lang.ClassClassLoaderResolver;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This class loads the configuration by scanning the classpath for packages and
 * action classes.
 * </p>
 *
 * TODO add ability to override actions from components and such
 *
 * @author  Brian Pontarelli
 */
@Singleton
@SuppressWarnings("unchecked")
public class DefaultActionConfigurationProvider implements ActionConfigurationProvider {
    private static final Logger logger = Logger.getLogger(DefaultActionConfigurationProvider.class.getName());
    public static final String ACTION_CONFIGURATION_KEY = "jcatapultActionConfiguration";
    private final ServletContext context;

    @Inject
    public DefaultActionConfigurationProvider(ServletContext context, ActionURIBuilder actionURIBuilder) {
        this.context = context;

        ClassClassLoaderResolver resolver = new ClassClassLoaderResolver();
        Set<Class<?>> actionClassses;
        try {
            actionClassses = resolver.findByLocators(new ClassClassLoaderResolver.AnnotatedWith(Action.class),
                true, array("org.jcatapult.mvc.*", "org.hibernate.*"), "action");
        } catch (IOException e) {
            throw new RuntimeException("Error discovering action classes", e);
        }

        Map<String, ActionConfiguration> configuration = new HashMap<String, ActionConfiguration>();
        for (Class<?> actionClass : actionClassses) {
            String uri = actionURIBuilder.build(actionClass);
            ActionConfiguration actionConfiguration = new DefaultActionConfiguration(actionClass, uri);
            configuration.put(uri, actionConfiguration);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Added action configuration for [" + actionClass + "] and the uri [" + uri + "]");
            }
        }

        context.setAttribute(ACTION_CONFIGURATION_KEY, configuration);
    }

    /**
     * {@inheritDoc}
     */
    public ActionConfiguration lookup(String uri) {
        Map<String, ActionConfiguration> configuration = (Map<String, ActionConfiguration>) context.getAttribute(ACTION_CONFIGURATION_KEY);
        if (configuration == null) {
            return null;
        }

        return configuration.get(uri);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ActionConfiguration> knownConfiguration() {
        return (Map<String, ActionConfiguration>) context.getAttribute(ACTION_CONFIGURATION_KEY);
    }
}