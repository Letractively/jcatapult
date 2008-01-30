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

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This class provides easy access to file manager configuration.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(FileConfigurationImpl.class)
public interface FileConfiguration {
    /**
     * @return  The configuration parameter named <code>file-mgr.file-servlet.dir</code> or the default value
     *          of <code>${user.home}/data<code>.
     */
    String getFileServletDir();

    /**
     * @return  The configuration parameter named <code>file-mgr.file-servlet.prefix</code> or the default value
     *          of <code>/files</code>.
     */
    String getFileServletPrefix();

    /**
     * @return  The configuration parameter named <code>file-mgr.file-upload.allowed-content-types</code> or the
     *          default value of <code>new String[]{"image/jpeg", "image/png", "image/gif",
     *          "application/x-shockwave-flash", "application/pdf"}</code>.
     */
    String[] getFileUploadAllowedContentTypes();

    /**
     * @return  The configuration parameter named <code>file-mgr.create-folder-allowed</code> or the default value
     *          of <code>true</code>.
     */
    boolean isCreateFolderAllowed();
}