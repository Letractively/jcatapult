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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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
    private List<File> files;

    @XmlElement(name = "Error")
    private Error error;

    @XmlElement(name = "UploadResult")
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

    public List<File> getFiles() {
        if (files == null) {
            files = new ArrayList<File>();
        }
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
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