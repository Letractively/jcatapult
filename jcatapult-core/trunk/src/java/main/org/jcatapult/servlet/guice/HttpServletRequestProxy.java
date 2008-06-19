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
package org.jcatapult.servlet.guice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.jcatapult.servlet.ServletObjectsHolder;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class HttpServletRequestProxy extends HttpServletRequestWrapper {
    public HttpServletRequestProxy() {
        super(ServletObjectsHolder.getServletRequest());
    }

    public String getAuthType() {
        initialize();
        return super.getAuthType();
    }

    public Cookie[] getCookies() {
        initialize();
        return super.getCookies();
    }

    public long getDateHeader(String s) {
        initialize();
        return super.getDateHeader(s);
    }

    public String getHeader(String s) {
        initialize();
        return super.getHeader(s);
    }

    public Enumeration getHeaders(String s) {
        initialize();
        return super.getHeaders(s);
    }

    public Enumeration getHeaderNames() {
        initialize();
        return super.getHeaderNames();
    }

    public int getIntHeader(String s) {
        initialize();
        return super.getIntHeader(s);
    }

    public String getMethod() {
        initialize();
        return super.getMethod();
    }

    public String getPathInfo() {
        initialize();
        return super.getPathInfo();
    }

    public String getPathTranslated() {
        initialize();
        return super.getPathTranslated();
    }

    public String getContextPath() {
        initialize();
        return super.getContextPath();
    }

    public String getQueryString() {
        initialize();
        return super.getQueryString();
    }

    public String getRemoteUser() {
        initialize();
        return super.getRemoteUser();
    }

    public boolean isUserInRole(String s) {
        initialize();
        return super.isUserInRole(s);
    }

    public Principal getUserPrincipal() {
        initialize();
        return super.getUserPrincipal();
    }

    public String getRequestedSessionId() {
        initialize();
        return super.getRequestedSessionId();
    }

    public String getRequestURI() {
        initialize();
        return super.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        initialize();
        return super.getRequestURL();
    }

    public String getServletPath() {
        initialize();
        return super.getServletPath();
    }

    public HttpSession getSession(boolean b) {
        initialize();
        return super.getSession(b);
    }

    public HttpSession getSession() {
        initialize();
        return super.getSession();
    }

    public boolean isRequestedSessionIdValid() {
        initialize();
        return super.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        initialize();
        return super.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        initialize();
        return super.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        initialize();
        return super.isRequestedSessionIdFromUrl();
    }

    public Object getAttribute(String s) {
        initialize();
        return super.getAttribute(s);
    }

    public Enumeration getAttributeNames() {
        initialize();
        return super.getAttributeNames();
    }

    public String getCharacterEncoding() {
        initialize();
        return super.getCharacterEncoding();
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        initialize();
        super.setCharacterEncoding(s);
    }

    public int getContentLength() {
        initialize();
        return super.getContentLength();
    }

    public String getContentType() {
        initialize();
        return super.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        initialize();
        return super.getInputStream();
    }

    public String getParameter(String s) {
        initialize();
        return super.getParameter(s);
    }

    public Map getParameterMap() {
        initialize();
        return super.getParameterMap();
    }

    public Enumeration getParameterNames() {
        initialize();
        return super.getParameterNames();
    }

    public String[] getParameterValues(String s) {
        initialize();
        return super.getParameterValues(s);
    }

    public String getProtocol() {
        initialize();
        return super.getProtocol();
    }

    public String getScheme() {
        initialize();
        return super.getScheme();
    }

    public String getServerName() {
        initialize();
        return super.getServerName();
    }

    public int getServerPort() {
        initialize();
        return super.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        initialize();
        return super.getReader();
    }

    public String getRemoteAddr() {
        initialize();
        return super.getRemoteAddr();
    }

    public String getRemoteHost() {
        initialize();
        return super.getRemoteHost();
    }

    public void setAttribute(String s, Object o) {
        initialize();
        super.setAttribute(s, o);
    }

    public void removeAttribute(String s) {
        initialize();
        super.removeAttribute(s);
    }

    public Locale getLocale() {
        initialize();
        return super.getLocale();
    }

    public Enumeration getLocales() {
        initialize();
        return super.getLocales();
    }

    public boolean isSecure() {
        initialize();
        return super.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        initialize();
        return super.getRequestDispatcher(s);
    }

    public String getRealPath(String s) {
        initialize();
        return super.getRealPath(s);
    }

    public int getRemotePort() {
        initialize();
        return super.getRemotePort();
    }

    public String getLocalName() {
        initialize();
        return super.getLocalName();
    }

    public String getLocalAddr() {
        initialize();
        return super.getLocalAddr();
    }

    public int getLocalPort() {
        initialize();
        return super.getLocalPort();
    }

    protected void initialize() {
        setRequest(ServletObjectsHolder.getServletRequest());
    }
}