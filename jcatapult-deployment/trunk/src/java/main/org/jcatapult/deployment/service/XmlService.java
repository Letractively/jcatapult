package org.jcatapult.deployment.service;

import java.io.File;

import com.google.inject.ImplementedBy;

/**
 * Interface service for unmarshalling xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
@ImplementedBy(XmlServiceImpl.class)
public interface XmlService<T> {

    /**
     * Unmarshall's an object V into a type T
     *
     * @param file the file to unmarshall
     * @return type T the type to return after unmarshalling
     * @throws XmlServiceException thrown if there's an error during the xml unmarshalling process
     */
    public T unmarshall(File file) throws XmlServiceException;

    /**
     * Marshall's type T into a File object
     *
     * @param type the type to marshall
     * @param object the marshalled object
     * @throws XmlServiceException thrown if there are errors during marshalling
     */
    public void marshall(T type, File object) throws XmlServiceException;
}
