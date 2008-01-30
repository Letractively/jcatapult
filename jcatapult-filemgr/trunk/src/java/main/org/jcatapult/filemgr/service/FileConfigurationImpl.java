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

import java.util.Arrays;

import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This implements the file configuration interface.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FileConfigurationImpl implements FileConfiguration {
    private Configuration configuration;

    @Inject
    public FileConfigurationImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    public String getFileServletDir() {
        return configuration.getString("file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data");
    }

    /**
     * {@inheritDoc}
     */
    public String getFileServletPrefix() {
        return configuration.getString("file-mgr.file-servlet.prefix", "/files");
    }

    /**
     * {@inheritDoc}
     */
    public String[] getFileUploadAllowedContentTypes() {
        String[] value = configuration.getStringArray("file-mgr.file-upload.allowed-content-types");
        if (value == null) {
            value = new String[]{"image/jpeg", "image/png", "image/gif", "application/x-shockwave-flash",
                "application/pdf"};
        }

        Arrays.sort(value);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCreateFolderAllowed() {
        return configuration.getBoolean("file-mgr.create-folder-allowed", true);
    }
}