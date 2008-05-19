package org.jcatapult.deployment.service;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

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
