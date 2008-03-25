package org.jcatapult.deployment.service;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jcatapult.deployment.domain.jaxb.Deploy;

/**
 * JAXB Xml Service for unmarshalling objects into jaxb context
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class JaxbXmlService implements XmlService {

    /**
     * {@inheritDoc}
     */
    public <Deploy> Deploy unmarshallXml(File xmlFile) throws XmlServiceException {

        Deploy deploy;

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("org.jcatapult.deployment.domain.jaxb", this.getClass().getClassLoader());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            deploy = (Deploy) unmarshaller.unmarshal(xmlFile);
        } catch (JAXBException e) {
            throw new XmlServiceException(e);
        }

        return deploy;
    }
}
