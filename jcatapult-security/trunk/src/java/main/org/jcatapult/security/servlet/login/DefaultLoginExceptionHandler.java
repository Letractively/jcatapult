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
package org.jcatapult.security.servlet.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jcatapult.security.JCatapultSecurityException;
import org.jcatapult.security.config.SecurityConfiguration;
import org.jcatapult.security.servlet.FacadeHttpServletRequest;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles login exceptions by creating a Struts request
 * that can be handled by a Struts action. In most cases the URI
 * for login is <code>/jcatapult-security-check</code>. This class
 * allows you to use a different URI to invoke a Struts action for
 * handling failed logins. The configuration parameter that controls
 * the failed login URI is <code>jcatapult.security.login.failed-uri</code>
 * The default for this configuration parameter is <b>/login-failed</b>.
 * </p>
 *
 * <p>
 * In addition, this class always places the exception that was thrown
 * into the HTTP request under the key <code>jcatapult.security.login.exception</code>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultLoginExceptionHandler implements LoginExceptionHandler {
    public static final String EXCEPTION_KEY = "jcatapult_security_login_exception";
    private final HttpServletRequest request;
    private final String failedLoginURI;

    @Inject
    public DefaultLoginExceptionHandler(HttpServletRequest request, SecurityConfiguration configuration) {
        this.request = request;
        this.failedLoginURI = configuration.getLoginFailedURI();
    }

    /**
     * {@inheritDoc}
     */
    public void handle(JCatapultSecurityException exception, WorkflowChain chain)
    throws IOException, ServletException {
        HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
        HttpServletRequest previous = (HttpServletRequest) wrapper.getRequest();
        FacadeHttpServletRequest facade = new FacadeHttpServletRequest(previous, failedLoginURI, null);
        wrapper.setRequest(facade);
        request.setAttribute(EXCEPTION_KEY, exception);
        chain.continueWorkflow();
    }
}