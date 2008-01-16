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
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles successful logins by creating a Struts request
 * that can be handled by a Struts action. In most cases the URI
 * for login is <code>/jcatapult-security-check</code>. This class
 * allows you to use a different URI to invoke a Struts action for
 * handling successful logins. The configuration parameter that controls
 * the successful login URI is <code>jcatapult.security.login.succesful-login-uri</code>
 * The default for this configuration parameter is <b>/successful-login</b>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultPostLoginHandler implements PostLoginHandler {
    private final String successfulLoginURI;

    @Inject
    public DefaultPostLoginHandler(Configuration configuration) {
        this.successfulLoginURI = configuration.getString("jcatapult.security.login.successful-login-uri", "/successful-login");
    }

    public void handle(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws ServletException, IOException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getRequestURI() {
                return successfulLoginURI;
            }

            @Override
            public String getServletPath() {
                return successfulLoginURI;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String uri) {
                final RequestDispatcher rd = httpRequest.getRequestDispatcher(uri);
                return new RequestDispatcher() {
                    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                        rd.forward(httpRequest, servletResponse);
                    }

                    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                        rd.include(httpRequest, servletResponse);
                    }
                };
            }
        };

        workflowChain.doWorkflow(wrapper, response);
    }
}