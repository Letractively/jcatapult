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
package org.jcatapult.security;

import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This class implements the SaltSource interface and provides a simple static
 * salt source using the configuration property named <code>jcatapult.security.saltSource.salt</code>.
 * If the salt is not configured, this returns <b>jcatapult</b> as a simple salt
 * that can be used during development. You should always change the salt source
 * before going to production.
 * </p>
 *
 * <p>
 * This uses the Commons Configuration API, which the API used by the JCatapult
 * environment aware configuration system.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ConfiguredSaltSource implements SaltSource {

    private static final Logger logger = Logger.getLogger(ConfiguredSaltSource.class.getName());

    private final Configuration configuration;

    @Inject
    public ConfiguredSaltSource(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    public String getSalt() {
        String salt = configuration.getString("jcatapult.security.salt");
        logger.finest("jCatapult Security using salt source [" + salt + "]");
        return salt;
    }

    /**
     * Returns the default.
     */
    public String getSalt(Object o) {
        return getSalt();
    }
}