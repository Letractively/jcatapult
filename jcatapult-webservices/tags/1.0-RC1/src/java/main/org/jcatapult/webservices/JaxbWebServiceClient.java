/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.webservices;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.google.inject.Inject;

/**
 * <p>
 * Implementation of {@link WebServiceClient} which uses JAXB to marshall/unmarshall XML
 * sent to or received from services.  Internally, this uses Apache Commons HttpClient to make
 * HTTP requests.
 * </p>
 *
 * <p>
 * See the peak6-iSeeds project for examples of using this class to call Wall Street On Demand services.
 * </p>
 *
 * @author Patrick Taylor
 */
public class JaxbWebServiceClient implements WebServiceClient {

    private static final Logger logger = Logger.getLogger(JaxbWebServiceClient.class.getName());

    private HttpClient httpClient;
    private JAXBContext jaxbContext;

    /**
     * Each client calls this constructor to specify the JAXB-generated package (or packages)
     * used to create a JAXBContext for the specific service.  Package names are separated
     * by colons.
     *
     * @param jaxbContextPath  A JAXB context path, such as "com.wsod.quote" or "com.wsod.news:com.wsod.newsstory".
     * @throws WebServiceException if a JAXBException is caught when attempting to construct the JAXBContext
     * @see JAXBContext#newInstance(String)
     */
    public JaxbWebServiceClient(String jaxbContextPath) {
        try {
            // see https://jaxb.dev.java.net/faq/index.html#classloader
            this.jaxbContext = JAXBContext.newInstance(jaxbContextPath, this.getClass().getClassLoader());
        } catch (JAXBException ex) {
            throw new WebServiceException("unable to create JAXBContext for " + jaxbContextPath, ex);
        }
    }

    /**
     * This method exists to inject the HttpClient used to make HTTP requests.
     */
    @Inject
    void setClient(HttpClient client) {
        this.httpClient = client;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T doGet(Class<T> responseType, String url) throws WebServiceException {
        logger.info("url=" + url);
        HttpMethod get = new GetMethod(url);
        try {
            // create an Unmarshaller to convert the XML response to JAXB objects
            // TODO: consider pooling these for performance
            // these are not thread safe: https://jaxb.dev.java.net/faq/index.html#threadSafety
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (httpClient.executeMethod(get) == HttpStatus.SC_OK) {

                // note that this stream is closed automatically by HttpClient either upon fully
                // reading the stream or by the call to get.releaseConnection() in the finally block
                InputStream is = get.getResponseBodyAsStream();

                // unmarshall the response
                Object response = unmarshaller.unmarshal(is);

                // If the schema defines only a single element of a simple type T,
                // such as <xs:element name="account" type="xs:string" />,
                // the unmarshalled object will be a JAXBElement<T>,
                // otherwise, the unmarshalled object will be of type T
                if (response instanceof JAXBElement) {
                    return ((JAXBElement<T>)response).getValue();
                } else {
                    return (T)response;
                }
            } else {
                throw new WebServiceException(String.valueOf(get.getStatusLine()));
            }
        } catch (WebServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WebServiceException(ex);
        } finally {
            get.releaseConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T doPost(Class<T> responseType, String url) throws WebServiceException {
        logger.info("url=" + url);
        HttpMethod post = new PostMethod(url);
        try {
            // create an Unmarshaller to convert the XML response to JAXB objects
            // TODO: consider pooling these for performance
            // these are not thread safe: https://jaxb.dev.java.net/faq/index.html#threadSafety
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (httpClient.executeMethod(post) == HttpStatus.SC_OK) {

                // note that this stream is closed automatically by HttpClient either upon fully
                // reading the stream or by the call to get.releaseConnection() in the finally block
                InputStream is = post.getResponseBodyAsStream();

                // unmarshall the response
                Object response = unmarshaller.unmarshal(is);

                // If the schema defines only a single element of a simple type T,
                // such as <xs:element name="account" type="xs:string" />,
                // the unmarshalled object will be a JAXBElement<T>,
                // otherwise, the unmarshalled object will be of type T
                if (response instanceof JAXBElement) {
                    return ((JAXBElement<T>)response).getValue();
                } else {
                    return (T)response;
                }
            } else {
                throw new WebServiceException(String.valueOf(post.getStatusLine()));
            }
        } catch (WebServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WebServiceException(ex);
        } finally {
            post.releaseConnection();
        }
    }
}
