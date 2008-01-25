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

    public FacadeHttpServletRequest(HttpServletRequest httpServletRequest, String uri,
            Map<String, String[]> parameters) {
        super(httpServletRequest);
        this.uri = uri;
        this.parameters = parameters;
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

        return super.getParameter(key);
    }

    public Map getParameterMap() {
        if (parameters != null) {
            Map<String, String[]> complete = new HashMap<String, String[]>(super.getParameterMap());
            complete.putAll(parameters);
            return complete;
        }

        return super.getParameterMap();
    }

    public Enumeration getParameterNames() {
        return new IteratorEnumeration(getParameterMap().keySet().iterator());
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