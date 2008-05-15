package org.jcatapult.deployment.service;

import java.io.File;

import com.google.inject.ImplementedBy;

/**
 * Interface service for unmarshalling xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public interface XmlService<T> {

    /**
     * Unmarshall's an object V into a type T
     *
     * @param file the file to unmarshall
     * @return type T the type to return after unmarshalling
     * @throws XmlServiceException thrown if there's an error during the xml unmarshalling process
     */
    public T unmarshall(File file) throws XmlServiceException;
}
