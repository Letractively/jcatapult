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

package org.jcatapult.filemgr.domain.fck;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * <p>
 * This class represents the current folder that the File Manager is
 * interacting with and is passed back to the client from the
 * server in order to specify the URL that the File Manager should use
 * for this folder.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrentFolder {
    @XmlAttribute()
    private String path;

    @XmlAttribute()
    private String url;

    public CurrentFolder() {
    }

    public CurrentFolder(String path, String url) {
        this.path = path;
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url != null && !url.endsWith("/")) {
            url += "/";
        }

        this.url = url;
    }
}