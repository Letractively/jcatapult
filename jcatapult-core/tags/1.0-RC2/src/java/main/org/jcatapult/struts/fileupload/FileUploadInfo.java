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
package org.jcatapult.struts.fileupload;

import java.io.File;

/**
 * <p>
 * This class stores the file upload information that Struts adds to the
 * stack in a nice encapsulated form.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileUploadInfo {
    private File file;
    private String name;
    private String contentType;

    public FileUploadInfo() {
    }

    public FileUploadInfo(File file, String name, String contentType) {
        this.file = file;
        this.name = name;
        this.contentType = contentType;
    }

    /**
     * @return  The File that was uploaded and stored on disk by Struts.
     */
    public File getFile() {
        return file;
    }

    /**
     * Set by Struts to the location on disk of the file that was uploaded. This name will most likely be a UUID of
     * sorts and not usable in general. Most applications will move the file to something specific or to the name given
     * via the {@link #getName()} method that is setup by Struts.
     *
     * @param   file The file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return  The name of the file that was specified by the user via the browser or some other HTTP interface.
     */
    public String getName() {
        return name;
    }

    /**
     * Set by Struts to the name of the file that the user selected in their browser or via some other HTTP interface.
     *
     * @param   name The name of the file.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  The content type of the file as it was determined by browser and specified in the HTTP multipart
     *          request header.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set by Struts to the content type that was sent from the browser or other HTTP interface.
     *
     * @param   contentType The content type of the file uploaded.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}