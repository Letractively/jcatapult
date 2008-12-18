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
 */
package org.jcatapult.security.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.java.util.IteratorEnumeration;

/**
 * <p>
 * This class allows the URI to be completely changed, including support for
 * forwards and includes from a RequestDispatcher.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FacadeHttpServletRequest extends HttpServletRequestWrapper {
    private final String uri;
    private final Map<String, String[]> parameters;
    private final boolean proxy;

    /**
     * Constructs a new request facade.
     *
     * @param   httpServletRequest The request to wrap.
     * @param   uri The new URI.
     * @param   parameters Any additional parameters.
     * @param   proxy Determines if the parameter lookups are proxied to the wrapped request. When
     *          this is true, they are proxied to the wrapped request if the parameter map passed to
     *          the constructor doesn't contain the parameter. If this is false, only the parameter
     *          map passed to the constructor is used.
     */
    public FacadeHttpServletRequest(HttpServletRequest httpServletRequest, String uri,
            Map<String, String[]> parameters, boolean proxy) {
        super(httpServletRequest);
        this.uri = uri;
        this.parameters = parameters;
        this.proxy = proxy;
    }

    @Override
    public String getRequestURI() {
        if (uri == null) {
            return super.getRequestURI();
        }

        return uri;
    }

    @Override
    public String getServletPath() {
        if (uri == null) {
            return super.getServletPath();
        }

        return uri;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String uri) {
        if (uri == null) {
            return super.getRequestDispatcher(uri);
        }

        HttpServletRequest httpRequest = (HttpServletRequest) super.getRequest();
        RequestDispatcher rd = httpRequest.getRequestDispatcher(uri);
        return new FacadeRequestDispatcher(rd, httpRequest);
    }

    public String getParameter(String key) {
        if (parameters != null && parameters.containsKey(key) && parameters.get(key) != null) {
            return parameters.get(key)[0];
        }

        if (proxy) {
            return super.getParameter(key);
        }

        return null;
    }

    public Map getParameterMap() {
        Map<String, String[]> complete = new HashMap<String, String[]>();
        if (parameters != null) {
            complete.putAll(parameters);
        }

        if (proxy) {
            complete.putAll(super.getParameterMap());
        }

        return complete;
    }

    public Enumeration getParameterNames() {
        Set<String> names = new HashSet<String>();
        if (parameters != null) {
            names.addAll(parameters.keySet());
        }

        if (proxy) {
            names.addAll(super.getParameterMap().keySet());
        }

        return new IteratorEnumeration(names.iterator());
    }

    public String[] getParameterValues(String key) {
        if (parameters != null && parameters.containsKey(key) && parameters.get(key) != null) {
            return parameters.get(key);
        }

        return super.getParameterValues(key);
    }

    public static class FacadeRequestDispatcher implements RequestDispatcher {
        private final RequestDispatcher requestDispatcher;
        private final HttpServletRequest httpRequest;

        public FacadeRequestDispatcher(RequestDispatcher requestDispatcher, HttpServletRequest httpRequest) {
            this.requestDispatcher = requestDispatcher;
            this.httpRequest = httpRequest;
        }

        public void forward(ServletRequest servletRequest, ServletResponse servletResponse)
        throws ServletException, IOException {
            requestDispatcher.forward(httpRequest, servletResponse);
        }

        public void include(ServletRequest servletRequest, ServletResponse servletResponse)
        throws ServletException, IOException {
            requestDispatcher.include(httpRequest, servletResponse);
        }
    }
}