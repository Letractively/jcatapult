
package org.jcatapult.webservices;

/**
 * Abstract base class for alternative or mock implementations of WebServiceClient.
 * Provides a default implementation of each interface method which returns null.
 *
 * @author Patrick Taylor
 */
public abstract class AbstractWebServiceClient implements WebServiceClient {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T doGet(Class<T> responseType, String url) throws WebServiceException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T doPost(Class<T> responseType, String url) throws WebServiceException {
        return null;
    }
}
