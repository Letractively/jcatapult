/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
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

package org.jcatapult.deployer.domain;

import java.io.File;

import org.apache.commons.configuration.Configuration;

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * <p>Bean to represent a project xml file.  specifically, the project name and its root directory</p>
 *
 * @author jhumphrey
 */
public class Project implements Validatable {
    private String name;
    private File dir;
    private String version;
    private Configuration config;

    /**
     * <p>Returns the project name.  This is extracted from the project's project.xml file
     * within the project 'name' attribute.</p>
     *
     * @return the project name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Sets the project name</p>
     *
     * @param name the project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Gets the project root directory</p>
     *
     * @return the project root directory
     */
    public File getDir() {
        return dir;
    }

    /**
     * <p>Sets the project root directory</p>
     *
     * @param dir the project root directory
     */
    public void setDir(File dir) {
        this.dir = dir;
    }

    /**
     * <p>Gets the current project version</p>
     *
     * @return the current project version
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>Sets the project version number</p>
     *
     * @param version the project version number
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * <p>Returns the apache commons configuration object.  This object can be used to access
     * any additional information from the project.xml file</p>
     *
     * @return apache commons configuration object.
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * <p>Sets the apache commons configuration object</p>
     *
     * @param config the apache commons configuration object
     */
    public void setConfig(Configuration config) {
        this.config = config;
    }

    /**
     * <p>validates the bean.  Required fields:</p>
     * <ul>
     *   <li>name</li>
     * </ul>
     *
     * @return returns a list of errors if invalid.  empty list otherwise
     */
    public ErrorList validate() {
        ErrorList errorList = new ErrorList();

        if (StringTools.isEmpty(name)) {
            errorList.addError("Project name must be defined in the project.xml");
        }

        return errorList;
    }
}
