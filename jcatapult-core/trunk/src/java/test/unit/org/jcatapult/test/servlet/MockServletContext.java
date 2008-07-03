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

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.java.util.IteratorEnumeration;

/**
 * <p>
 * This is a mock servlet context.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class MockServletContext implements ServletContext {
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private File webDir;

    public MockServletContext() {
        webDir = new File("web");
        if (!webDir.isDirectory()) {
            webDir = new File("src/web/test");
            if (!webDir.isDirectory()) {
                throw new RuntimeException("Not testing in a web application or Module and webDir " +
                    "was not passed to the MockServletContext in the constructor");
            }
        }
    }

    public MockServletContext(File webDir) {
        this.webDir = webDir;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return new IteratorEnumeration(attributes.keySet().iterator());
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public ServletContext getContext(String s) {
        return null;
    }

    public int getMajorVersion() {
        return 0;
    }

    public int getMinorVersion() {
        return 0;
    }

    public String getMimeType(String s) {
        return null;
    }

    public Set getResourcePaths(String s) {
        return null;
    }

    public URL getResource(String path) throws MalformedURLException {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        File f = new File(webDir, path);
        if (f.isFile()) {
            return f.toURI().toURL();
        }

        return null;
    }

    public InputStream getResourceAsStream(String path) {
        try {
            URL url = getResource(path);
            if (url != null) {
                return url.openStream();
            }
        } catch (Exception e) {
        }

        return null;
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        return null;
    }

    public Servlet getServlet(String s) throws ServletException {
        return null;
    }

    public Enumeration getServlets() {
        return null;
    }

    public Enumeration getServletNames() {
        return null;
    }

    public void log(String s) {
    }

    public void log(Exception e, String s) {
    }

    public void log(String s, Throwable throwable) {
    }

    public String getRealPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        File f = new File(webDir, path);
        if (f.isFile()) {
            return f.getAbsolutePath();
        }

        return null;
    }

    public String getServerInfo() {
        return null;
    }

    public String getInitParameter(String s) {
        return null;
    }

    public Enumeration getInitParameterNames() {
        return null;
    }

    public String getServletContextName() {
        return null;
    }
}