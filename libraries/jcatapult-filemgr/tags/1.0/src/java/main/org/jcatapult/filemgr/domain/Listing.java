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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * This class stores the listing of a directory.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@XmlRootElement(name = "listing")
@XmlAccessorType(XmlAccessType.FIELD)
public class Listing implements Serializable {
    private static final long serialVersionUID = 1;

    @XmlAttribute()
    private String path;

    @XmlAttribute()
    private String uri;
    
    @XmlElement(name = "file")
    private final List<FileData> files = new ArrayList<FileData>();

    @XmlElement(name = "directory")
    private final List<DirectoryData> directories = new ArrayList<DirectoryData>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void addFile(FileData fileData) {
        files.add(fileData);
    }

    public List<DirectoryData> getDirectories() {
        return directories;
    }

    public void addDirectory(DirectoryData directoryData) {
        directories.add(directoryData);
    }
}
