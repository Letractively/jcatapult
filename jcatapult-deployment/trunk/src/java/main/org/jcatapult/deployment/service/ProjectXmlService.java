package org.jcatapult.deployment.service;

import java.io.File;
import java.util.List;

import org.jcatapult.deployment.domain.Project;
import org.jcatapult.deployment.domain.DeploymentProperties;
import org.jcatapult.deployment.domain.Deploy;
import org.jcatapult.deployment.domain.Environment;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class ProjectXmlService extends CommonsConfigurationXmlService<Project> {

    /**
     * {@inheritDoc}
     */
    public Project unmarshall(File file) throws XmlServiceException {
        init(file);

        Project project = new Project();
        project.setName(getConfig().getString("[@name]"));

        return project;
    }
}
