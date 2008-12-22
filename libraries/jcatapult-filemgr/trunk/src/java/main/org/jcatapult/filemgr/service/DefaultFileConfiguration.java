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

import org.jcatapult.config.Configuration;
import org.jcatapult.mvc.parameter.fileupload.DefaultFileUploadWorkflow;

import com.google.inject.Inject;

/**
 * <p>
 * This implements the file configuration interface.
 * </p>
 *
 * @author Brian Pontarelli
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
    public String getFileServletDir() {
        return configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data");
    }

    /**
     * {@inheritDoc}
     */
    public String getFileServletPrefix() {
        return configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files");
    }

    /**
     * {@inheritDoc}
     */
    public String[] getFileUploadAllowedContentTypes() {
        String[] value = configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types");
        if (value == null || value.length == 0) {
            value = DefaultFileUploadWorkflow.DEFAULT_TYPES;
        }

        Arrays.sort(value);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCreateFolderAllowed() {
        return configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true);
    }
}