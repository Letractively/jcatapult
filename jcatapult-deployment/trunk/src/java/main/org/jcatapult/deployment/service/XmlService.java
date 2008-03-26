package org.jcatapult.deployment.service;

import com.google.inject.ImplementedBy;

/**
 * Interface service for unmarshalling xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
@ImplementedBy(XmlServiceImpl.class)
public interface XmlService<T, V> {

    /**
     * Unmarshall's an object V into a type T
     *
     * @param object S the oject to unmarshall
     * @return type T the type to return after unmarshalling
     * @throws XmlServiceException thrown if there's an error during the xml unmarshalling process
     */
    public T unmarshall(V object) throws XmlServiceException;

    /**
     * Marshall's type T into type S
     *
     * @param type the type to marshall
     * @param object the marshalled object
     * @throws XmlServiceException thrown if there are errors during marshalling
     */
    public void marshall(T type, V object) throws XmlServiceException;
}
