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
package org.jcatapult.filemgr.domain;

import java.io.Serializable;

/**
 * <p>
 * This class stores the result of an invocation to the
 * {@link org.jcatapult.filemgr.service.FileManagerService#upload(java.io.File,String,String,String)} method.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class UploadResult implements Serializable {
    private static final long serialVersionUID = 1;
    private String modifiedFileName;
    private String fileURL;
    private int resultCode;

    public UploadResult() {
    }

    public UploadResult(int resultCode) {
        this.modifiedFileName = null;
        this.fileURL = null;
        this.resultCode = resultCode;
    }

    public UploadResult(String modifiedFileName, String fileURL, boolean fileNameModified) {
        this.modifiedFileName = modifiedFileName;
        this.fileURL = fileURL;
        this.resultCode = fileNameModified ? 201 : 0;
    }

    public String getModifiedFileName() {
        return modifiedFileName;
    }

    public void setModifiedFileName(String modifiedFileName) {
        this.modifiedFileName = modifiedFileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}