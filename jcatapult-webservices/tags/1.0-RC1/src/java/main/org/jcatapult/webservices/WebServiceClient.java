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
 * An interface used to invoke web services.
 *
 * @author Patrick Taylor
 */
public interface WebServiceClient {

    /**
     * This method is called to invoke a web service using a GET method.
     * The given URL is constructed by the caller and should include all query parameters
     * necessary to invoke a specific service.  This method performs a GET on the url,
     * unmarshals the XML response, and returns the unmarshalled object, cast to the specified type.
     *
     * @param responseType  The expected type of the response.
     *      Use Object.class if the response can be of different types and the caller
     *      will be responsible for casting the response appropriately.
     * @param url  The complete URL on which to do a GET.
     * @return The object unmarshalled from the response XML.
     * @throws WebServiceException if any exceptions from the underlying subsystems
     *      are caught, or if the GET method returns an HTTP status code other than 200 (OK)
     */
    <T> T doGet(Class<T> responseType, String url) throws WebServiceException;

    /**
     * This method is called to invoke a web service using a POST method
     * when all parameters can be specified in the query string portion of the URL.
     * The given URL is constructed by the caller and should include all query parameters
     * necessary to invoke a specific service.  This method performs a POST on the url,
     * unmarshals the XML response, and returns the unmarshalled object, cast to the specified type.
     *
     * @param responseType  The expected type of the response.
     *      Use Object.class if the response can be of different types and the caller
     *      will be responsible for casting the response appropriately.
     * @param url  The complete URL on which to do a POST.
     * @return The object unmarshalled from the response XML.
     * @throws WebServiceException if any exceptions from the underlying subsystems
     *      are caught, or if the POST method returns an HTTP status code other than 200 (OK)
     */
    <T> T doPost(Class<T> responseType, String url) throws WebServiceException;
}