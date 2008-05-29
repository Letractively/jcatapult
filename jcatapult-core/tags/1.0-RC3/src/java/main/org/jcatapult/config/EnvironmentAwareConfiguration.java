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
package org.jcatapult.config;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.environment.EnvironmentResolver;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * <p>
 * This class is an Apache configuration that is environment aware.
 * In order to set this up to work correctly, there needs to be a
 * method for resolving the environment. Usually this is handled via
 * a JNDI entry, because it is simplest. In fact, the default resolver
 * uses this method, so unless you need to override that behavior,
 * you should be fine. If you need something more specific, you'll
 * need to implement the {@link EnvironmentResolver} interface.
 * </p>
 *
 * <p>
 * This class loads up configuration using a simple hierarchical
 * system. The default configuration is loaded first. This configuration
 * file is named <code>config-default.xml</code>. Then the environment
 * specific configuration is loaded next using the environment
 * resolver. The name of the environment is then appended to the file
 * name to find the environment configuration file like this:
 * </p>
 *
 * <pre>
 * config-&lt;environment>.xml
 * </pre>
 *
 * <p>
 * This file is then loaded, which means that the configuration in
 * that file takes precendence.
 * </p>
 *
 * <p>
 * The location of the configuration is specified using the JCatapult
 * property <strong>jcatapult.config.location</strong>.
 * </p>
 *
 * <p>
 * However, if the application is stored in a WAR, than the configuration is
 * loaded via the classpath using the prefix:
 * </p>
 *
 * <pre>
 * /config
 * </pre>
 *
 * @author  Brian Pontarelli
 */
public class EnvironmentAwareConfiguration extends CompositeConfiguration {
    private static final Logger logger = Logger.getLogger(EnvironmentAwareConfiguration.class.getName());

    /**
     * Constructs a new configuration.
     *
     * @param   environmentResolver The environment resolver to use for loading the configuration.
     * @param   resolver The path resolver used to find the configuration files.
     * @param   location The location of the configuration files.
     * @throws  ConfigurationException If the configuration could not be loaded.
     */
    @Inject
    public EnvironmentAwareConfiguration(EnvironmentResolver environmentResolver,
            ContainerResolver resolver, @Named("jcatapult.config.location") String location)
    throws ConfigurationException {
        // Fix location
        if (!location.endsWith("/")) {
            location = location + '/';
        }

        // Try to use the ServletContext to load the default configuration
        String locationRealPath = resolver.getRealPath(location + "config-default.xml");
        File defaultConfig = locationRealPath != null ? new File(locationRealPath) : null;
        if (defaultConfig != null && defaultConfig.exists() && defaultConfig.isFile()) {
            logger.info("Loading JCatapult configuration file [" + defaultConfig.getAbsolutePath() + "]");
            super.addConfiguration(new XMLConfiguration(defaultConfig));
        } else {
            URL url = this.getClass().getResource("/config/config-default.xml");
            if (url != null) {
                logger.info("Loading JCatapult configuration from classpath entry [/config/config-default.xml]");
                super.addConfiguration(new XMLConfiguration(url));
            }
        }

        // Try to use the ServletContext to load the environment configuration
        String envFile = "config-" + environmentResolver.getEnvironment() + ".xml";
        locationRealPath = resolver.getRealPath(location + envFile);
        File envConfig = locationRealPath != null ? new File(locationRealPath) : null;
        if (envConfig != null && envConfig.exists() && envConfig.isFile()) {
            logger.info("Loading JCatapult configuration file [" + envConfig.getAbsolutePath() + "]");
            super.addConfiguration(new XMLConfiguration(envConfig));
        } else {
            URL url = this.getClass().getResource("/config/" + envFile);
            if (url != null) {
                logger.info("Loading JCatapult configuration from classpath entry [/config/" + envFile + "]");
                super.addConfiguration(new XMLConfiguration(url));
            }
        }
    }
}