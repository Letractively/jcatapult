package org.jcatapult.deployment.service;

import java.io.File;

import org.jcatapult.deployment.domain.Project;

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
        project.setDir(file.getParentFile());

        return project;
    }
}
