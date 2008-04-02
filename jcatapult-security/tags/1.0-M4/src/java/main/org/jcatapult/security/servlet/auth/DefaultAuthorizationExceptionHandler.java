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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
    private final String notAuthorizedURL;

    @Inject
    public DefaultAuthorizationExceptionHandler(SecurityConfiguration configuration) {
        this.notAuthorizedURL = configuration.getRestrictedURI();
    }

    public void handle(AuthorizationException exception, ServletRequest request, ServletResponse response,
        WorkflowChain workflowChain)
    throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        FacadeHttpServletRequest wrapper = new FacadeHttpServletRequest(httpRequest, notAuthorizedURL, null);
        httpRequest.setAttribute(EXCEPTION_KEY, exception);
        workflowChain.doWorkflow(wrapper, response);
    }
}