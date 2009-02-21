/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 */
package org.jcatapult.test.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import static java.util.Arrays.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.java.util.IteratorEnumeration;

/**
 * <p>
 * This class is a mock servlet request.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class MockHttpServletRequest implements HttpServletRequest {
    protected final Map<String, Object> attributes = new HashMap<String, Object>();
    protected final Map<String, List<String>> headers = new HashMap<String, List<String>>();
    protected final MockHttpSession session;
    protected final Map<String, List<String>> parameters;
    protected String contentType = null;
    protected String uri;
    protected Locale locale;
    protected boolean post;
    protected String encoding;
    protected MockRequestDispatcher dispatcher;

    protected String remoteAddr = "127.0.0.1";

    public MockHttpServletRequest(String uri, Locale locale, boolean post, String encoding,
            MockServletContext context) {
        this.parameters = new HashMap<String, List<String>>();
        this.uri = uri;
        this.locale = locale;
        this.post = post;
        this.encoding = encoding;
        this.session = new MockHttpSession(context);

        if (post) {
            contentType = "application/x-www-form-urlencoded";
        }
    }

    public MockHttpServletRequest(String uri, Locale locale, boolean post, String encoding,
            MockHttpSession session) {
        this.parameters = new HashMap<String, List<String>>();
        this.uri = uri;
        this.locale = locale;
        this.post = post;
        this.encoding = encoding;
        this.session = session;

        if (post) {
            contentType = "application/x-www-form-urlencoded";
        }
    }

    public MockHttpServletRequest(Map<String, List<String>> parameters, String uri, String encoding,
            Locale locale, boolean post, MockHttpSession session) {
        this.parameters = parameters;
        this.uri = uri;
        this.encoding = encoding;
        this.locale = locale;
        this.post = post;
        this.session = session;

        if (post) {
            contentType = "application/x-www-form-urlencoded";
        }
    }

    public MockHttpServletRequest(Map<String, List<String>> parameters, String uri, String encoding,
            Locale locale, boolean post, MockServletContext context) {
        this.parameters = parameters;
        this.uri = uri;
        this.encoding = encoding;
        this.locale = locale;
        this.post = post;
        this.session = new MockHttpSession(context);

        if (post) {
            contentType = "application/x-www-form-urlencoded";
        }
    }


    //-------------------------------------------------------------------------
    //  javax.servlet.ServletRequest methods
    //-------------------------------------------------------------------------

    /**
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * The attribute names.
     */
    public Enumeration getAttributeNames() {
        return new IteratorEnumeration(attributes.keySet().iterator());
    }

    /**
     * Returns the encoding which defaults to null unless it is set
     */
    public String getCharacterEncoding() {
        return encoding;
    }

    /**
     * This should set a new character encoding
     */
    public void setCharacterEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     */
    public int getContentLength() {
        return -1;
    }

    /**
     */
    public String getContentType() {
        return contentType;
    }

    /**
     */
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     */
    public Enumeration getLocales() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return  The parameter or null.
     */
    public String getParameter(String name) {
        List<String> list = parameters.get(name);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public Map getParameterMap() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        for (String key : parameters.keySet()) {
            params.put(key, parameters.get(key).toArray(new String[parameters.get(key).size()]));
        }

        return params;
    }

    public Enumeration getParameterNames() {
        return new IteratorEnumeration(parameters.keySet().iterator());
    }

    public String[] getParameterValues(String name) {
        List<String> list = parameters.get(name);
        if (list != null) {
            return list.toArray(new String[list.size()]);
        }

        return null;
    }

    /**
     */
    public String getProtocol() {
        return "HTTP/1.0";
    }

    /**
     */
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    public String getRealPath(String url) {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    /**
     */
    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getRequestDispatcher(String thePath) {
        if (thePath == null) {
            return null;
        }

        String fullPath;

        // The spec says that the path can be relative, in which case it will
        // be relative to the request. So for relative paths, we need to take
        // into account the simulated URL (ServletURL).
        if (thePath.startsWith("/")) {

            fullPath = thePath;

        } else {

            String pI = getPathInfo();
            if (pI == null) {
                fullPath = catPath(getServletPath(), thePath);
            } else {
                fullPath = catPath(getServletPath() + pI, thePath);
            }

            if (fullPath == null) {
                return null;
            }
        }

        dispatcher = new MockRequestDispatcher(fullPath);
        return dispatcher;
    }

    /**
     */
    public String getScheme() {
        return "HTTP";
    }

    /**
     */
    public String getServerName() {
        return "localhost";
    }

    /**
     */
    public int getServerPort() {
        return 80;
    }

    /**
     */
    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }


    //-------------------------------------------------------------------------
    //  javax.servlet.http.HttpServletRequest methods
    //-------------------------------------------------------------------------


    /**
     * Local clients don't authenticate
     */
    public String getAuthType() {
        return null;
    }

    public String getContextPath() {
        return "";
    }

    public Cookie[] getCookies() {
        return null;
    }

    /**
     */
    public long getDateHeader(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.size() == 0) {
            return -1;
        }

        return Long.parseLong(values.get(0));
    }

    /**
     */
    public String getHeader(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    /**
     */
    public Enumeration getHeaderNames() {
        return new IteratorEnumeration(headers.keySet().iterator());
    }

    /**
     */
    public Enumeration getHeaders(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.size() == 0) {
            return new IteratorEnumeration(Collections.emptyList().iterator());
        }

       return new IteratorEnumeration(values.iterator());
    }

    /**
     */
    public int getIntHeader(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.size() == 0) {
            return -1;
        }

        return Integer.parseInt(values.get(0));
    }

    /**
     */
    public String getMethod() {
        return (post) ? "POST" : "GET";
    }

    /**
     */
    public String getPathInfo() {
        return "";
    }

    /**
     */
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public String getQueryString() {
        return "";
    }

    /**
     */
    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public String getRequestURI() {
        return uri;
    }

    /**
     */
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public String getServletPath() {
        return "";
    }

    /**
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     */
    public HttpSession getSession(boolean create) {
        return session;
    }

    /**
     */
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    /**
     */
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    public int getRemotePort() {
        return 0;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }

    //-------------------------------------------------------------------------
    //                            Helper methods
    //-------------------------------------------------------------------------


    /**
     * Will concatenate 2 paths, normalising it. For example :
     * ( /a/b/c + d = /a/b/d, /a/b/c + ../d = /a/d ). Code borrowed from
     * Tomcat 3.2.2 !
     *
     * @param theLookupPath the first part of the path
     * @param thePath the part to add to the lookup path
     * @return the concatenated thePath or null if an error occurs
     */
    String catPath(String theLookupPath, String thePath) {
        // Cut off the last slash and everything beyond
        int index = theLookupPath.lastIndexOf("/");
        if (index == -1) {
            return thePath;
        }

        theLookupPath = theLookupPath.substring(0, index);

        // Deal with .. by chopping dirs off the lookup thePath
        while (thePath.startsWith("../")) {
            if (theLookupPath.length() > 0) {
                index = theLookupPath.lastIndexOf("/");
                theLookupPath = theLookupPath.substring(0, index);
            } else {
                // More ..'s than dirs, return null
                return null;
            }

            index = thePath.indexOf("../") + 3;
            thePath = thePath.substring(index);
        }

        return theLookupPath + "/" + thePath;
    }


    //-------------------------------------------------------------------------
    //                          Modification Methods
    //-------------------------------------------------------------------------

    /**
     * Sets the content type of the request.
     *
     * @param   contentType The new content type.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Allows a header to be added.
     *
     * @param   name The header name.
     * @param   value The header value.
     */
    public void addHeader(String name, String value) {
        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            headers.put(name, values);
        }

        values.add(value);
    }

    /**
     * Sets the request parameter with the given name to the given value
     */
    public void setParameter(String name, String value) {
        List<String> list = parameters.get(name);
        if (list == null) {
            list = new ArrayList<String>();
            parameters.put(name, list);
        }

        list.add(value);
    }

    /**
     * Removes all the values of the request parameter with the given name
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * Clears all the parameters
     */
    public void clearParameters() {
        parameters.clear();
    }

    /**
     * Sets the request parameter with the given name to the given values
     */
    public void setParameters(String name, String... values) {
        parameters.put(name, asList(values));
    }

    /**
     * Clears all the attributes
     */
    public void clearAttributes() {
        attributes.clear();
    }

    /**
     * Returns the RequestDispatcher if one was created from this Request
     */
    public MockRequestDispatcher getRequestDispatcher() {
        return dispatcher;
    }

    /**
     * Modifies the request URI.
     *
     * @param   uri The request URI.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Modifies the locale.
     *
     * @param   locale The locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Sets whether or not the request is a POST or a GET.
     *
     * @param   post True for a POST.
     */
    public void setPost(boolean post) {
        this.post = post;

        if (post) {
            contentType = "application/x-www-form-urlencoded";
        } else {
            contentType = null;
        }
    }

    /**
     * Modifies the encoding.
     *
     * @param   encoding The encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}