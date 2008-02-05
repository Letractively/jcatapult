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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.security.JCatapultSecurityException;
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
    private final String failedLoginURI;

    @Inject
    public DefaultLoginExceptionHandler(Configuration configuration) {
        this.failedLoginURI = configuration.getString("jcatapult.security.login.failed-uri", "/login-failed");
    }

    /**
     * {@inheritDoc}
     */
    public void handle(JCatapultSecurityException exception, ServletRequest request,
            ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        FacadeHttpServletRequest wrapper = new FacadeHttpServletRequest(httpRequest, failedLoginURI, null);
        httpRequest.setAttribute(EXCEPTION_KEY, exception);
        workflowChain.doWorkflow(wrapper, response);
    }
}