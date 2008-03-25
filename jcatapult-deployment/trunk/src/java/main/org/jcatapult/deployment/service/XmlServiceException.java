package org.jcatapult.deployment.service;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class XmlServiceException extends Exception {
    public XmlServiceException() {
        super();
    }

    public XmlServiceException(String message) {
        super(message);
    }

    public XmlServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlServiceException(Throwable cause) {
        super(cause);
    }
}
