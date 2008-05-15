package org.jcatapult.deployment.service;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.jcatapult.deployment.domain.Project;
import org.jcatapult.deployment.domain.Deploy;
import org.jcatapult.deployment.domain.Environment;

/**
 * Abstract interface for processing xml documents using apache commons configuration
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public abstract class CommonsConfigurationXmlService<T> implements XmlService<T> {

    private XMLConfiguration config;

    public void init(File file) throws XmlServiceException {
        try {
            config = new XMLConfiguration(file);
        }
        catch (ConfigurationException cex) {
            throw new XmlServiceException(cex);
        }
    }

    public XMLConfiguration getConfig() {
        return config;
    }
}
