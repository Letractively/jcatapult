package org.jcatapult.deployment.service;

import java.io.File;

import com.google.inject.ImplementedBy;

/**
 * Interface service for unmarshalling xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
@ImplementedBy(JaxbXmlService.class)
public interface XmlService {
    /**
     * Unmarshall's an xml file into type T. T is defined by the concrete implementor
     *
     * @param xmlFile the xml file to unmarshall
     * @return type T
     * @throws XmlServiceException thrown if there's an error during the xml unmarshalling process
     */
    public <T> T unmarshallXml(File xmlFile) throws XmlServiceException;
}
