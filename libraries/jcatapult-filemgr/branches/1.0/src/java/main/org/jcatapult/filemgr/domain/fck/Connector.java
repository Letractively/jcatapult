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
 *
 */
package org.jcatapult.filemgr.domain.fck;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * This class represents the root element of the File Manager's expected
 * XML response.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@XmlRootElement(name = "Connector")
@XmlAccessorType(XmlAccessType.FIELD)
public class Connector {
    @XmlElement(name = "CurrentFolder")
    private CurrentFolder currentFolder;

    @XmlElementWrapper(name = "Folders")
    @XmlElement(name = "Folder")
    private List<Folder> folders;

    @XmlElementWrapper(name = "Files")
    @XmlElement(name = "File")
    private List<FileData> files;

    @XmlElement(name = "Error")
    private ErrorData error;

    @XmlTransient
    private UploadResult uploadResult;

    @XmlAttribute()
    private String command;

    @XmlAttribute()
    private String resourceType;

    public CurrentFolder getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(CurrentFolder currentFolder) {
        this.currentFolder = currentFolder;
    }

    public List<Folder> getFolders() {
        if (folders == null) {
            folders = new ArrayList<Folder>();
        }
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public List<FileData> getFiles() {
        if (files == null) {
            files = new ArrayList<FileData>();
        }
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public ErrorData getError() {
        return error;
    }

    public void setError(ErrorData error) {
        this.error = error;
    }

    public boolean isError() {
        return error != null;
    }

    public UploadResult getUploadResult() {
        return uploadResult;
    }

    public void setUploadResult(UploadResult uploadResult) {
        this.uploadResult = uploadResult;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}