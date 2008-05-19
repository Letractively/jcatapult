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

package org.jcatapult.deployer.service;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.jcatapult.deployer.domain.Project;

/**
 * <p>Unmarshalls xml via the apache commons XMLConfiguration object</p>
 *
 * @author jhumphrey
 */
public class ProjectXmlService {

    /**
     * <p>Unmashall's the project's project.xml file and returns a {@link org.jcatapult.deployer.domain.Project} bean</p>
     *
     * <p>The only property that currently gets parsed from the project.xml file is the name attribte
     * defined witin the root project element.</p>
     *
     * @param file the project.xml file
     * @return {@link org.jcatapult.deployer.domain.Project} bean
     * @throws XmlServiceException thrown if there's a problem during unmarshalling
     */
    public Project unmarshall(File file) throws XmlServiceException {

        Configuration config;
        try {
            config = new XMLConfiguration(file);
        }
        catch (ConfigurationException cex) {
            throw new XmlServiceException(cex);
        }

        Project project = new Project();
        project.setName(config.getString("[@name]"));
        project.setDir(file.getParentFile());

        return project;
    }
}
