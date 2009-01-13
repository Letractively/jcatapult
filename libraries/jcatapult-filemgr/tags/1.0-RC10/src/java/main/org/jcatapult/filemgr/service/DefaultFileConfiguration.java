/*
 * Copyright (c) 2001-2007, Inversoft, All Rights Reserved
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

package org.jcatapult.filemgr.service;

import com.google.inject.Inject;
import org.jcatapult.config.Configuration;

/**
 * <p>
 * This implements the file configuration interface.
 * </p>
 *
 * @author Brian Pontarelli and James Humphrey
 */
public class DefaultFileConfiguration implements FileConfiguration {
    private Configuration configuration;

    @Inject
    public DefaultFileConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    public String getFileStorageDir() {
        return configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data");
    }

    /**
     * {@inheritDoc}
     */
    public String getFileServletPrefix() {
        return configuration.getString("jcatapult.file-mgr.servlet-prefix", "/files");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCreateFolderAllowed() {
        return configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true);
    }
}