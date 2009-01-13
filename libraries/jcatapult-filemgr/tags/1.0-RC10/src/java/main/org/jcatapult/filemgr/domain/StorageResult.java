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

import java.io.File;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * This class stores the result of an invocation to the
 * {@link org.jcatapult.filemgr.service.FileManagerService#store(java.io.File, String, String, String)} method.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@XmlRootElement(name = "upload")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageResult implements Serializable {
    private static final long serialVersionUID = 1;

    @XmlAttribute()
    private String modifiedFileName;

    @XmlAttribute()
    private String fileURI;

    @XmlAttribute()
    private boolean changedFileName;

    @XmlTransient()
    private File file;

    @XmlAttribute()
    private int error;

    public StorageResult() {
    }

    public StorageResult(String modifiedFileName, String fileURI, boolean changedFileName, File file) {
        this.modifiedFileName = modifiedFileName;
        this.fileURI = fileURI;
        this.changedFileName = changedFileName;
        this.file = file;
    }

    public String getModifiedFileName() {
        return modifiedFileName;
    }

    public void setModifiedFileName(String modifiedFileName) {
        this.modifiedFileName = modifiedFileName;
    }

    public String getFileURI() {
        return fileURI;
    }

    public void setFileURI(String fileURI) {
        this.fileURI = fileURI;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public boolean isChangedFileName() {
        return changedFileName;
    }

    public void setChangedFileName(boolean changedFileName) {
        this.changedFileName = changedFileName;
    }
}