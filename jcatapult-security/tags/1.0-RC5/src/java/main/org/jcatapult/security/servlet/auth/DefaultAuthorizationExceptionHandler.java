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
package org.jcatapult.security.servlet.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jcatapult.security.auth.AuthorizationException;
import org.jcatapult.security.config.SecurityConfiguration;
import org.jcatapult.security.servlet.FacadeHttpServletRequest;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles authorization exceptions that are thrown by
 * the implementation of the {@link org.jcatapult.security.auth.Authorizer}
 * that are caught by the {@link AuthorizationWorkflow}. This class
 * handles the exception by creating a request that is handled by Struts
 * action. The URI for the action that is invoked by Struts is controlled
 * by the configuration parameter named <code>jcatapult.security.authorization.restricted-uri</code>.
 * The default URI is <code>/not-authorized</code>.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultAuthorizationExceptionHandler implements AuthorizationExceptionHandler {
    public static final String EXCEPTION_KEY = "jcatapult_authorization_exception";
    private final HttpServletRequest request;
    private final String notAuthorizedURL;

    @Inject
    public DefaultAuthorizationExceptionHandler(HttpServletRequest request, SecurityConfiguration configuration) {
        this.request = request;
        this.notAuthorizedURL = configuration.getRestrictedURI();
    }

    public void handle(AuthorizationException exception, WorkflowChain chain) throws ServletException, IOException {
        HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
        HttpServletRequest previous = (HttpServletRequest) wrapper.getRequest();
        FacadeHttpServletRequest facade = new FacadeHttpServletRequest(previous, notAuthorizedURL, null);
        wrapper.setRequest(facade);
        request.setAttribute(EXCEPTION_KEY, exception);
        chain.continueWorkflow();
    }
}