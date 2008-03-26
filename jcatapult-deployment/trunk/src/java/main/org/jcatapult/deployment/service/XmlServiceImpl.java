package org.jcatapult.deployment.service;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;

import org.jcatapult.deployment.domain.jaxb.Deploy;

/**
 * JAXB Xml Service for unmarshalling xml into jaxb context
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class XmlServiceImpl implements XmlService<Deploy> {

    private JAXBContext jaxbContext;

    public XmlServiceImpl() {
        try {
            jaxbContext = JAXBContext.newInstance("org.jcatapult.deployment.domain.jaxb", this.getClass().getClassLoader());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Deploy unmarshall(File file) throws XmlServiceException {

        Deploy deploy;

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            deploy = (Deploy) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new XmlServiceException(e);
        }

        return deploy;
    }

    /**
     * {@inheritDoc}
     */
    public void marshall(Deploy deploy, File file) throws XmlServiceException {
        throw new RuntimeException("Not Implemented");
    }
}
