package org.jcatapult.deployment.service;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Uses apache commons configuration to extract properties from the deploy.xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class XmlServiceImpl implements XmlService<Configuration> {

    /**
     * {@inheritDoc}
     */
    public Configuration unmarshall(File file) throws XmlServiceException {

        try {
            return new XMLConfiguration(file);
        }
        catch (ConfigurationException cex) {
            throw new XmlServiceException(cex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void marshall(Configuration c, File file) throws XmlServiceException {
        throw new RuntimeException("Not Implemented");
    }
}
