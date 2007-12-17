/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.webservices;

/**
 * This runtime exception class is used by the web services.
 * It may be used to wrap exceptions from the underlying layers,
 * such as IOException or JAXBException or AmazonECSException.
 *
 * @author Patrick Taylor
 */
public class WebServiceException extends RuntimeException {

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(Throwable cause) {
        super(cause);
    }
}
