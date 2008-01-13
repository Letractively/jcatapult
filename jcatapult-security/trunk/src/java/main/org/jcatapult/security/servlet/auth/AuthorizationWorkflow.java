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
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;
import org.jcatapult.security.auth.Authorizer;
import org.jcatapult.security.auth.UnauthorizedException;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.SecurityContext;
import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This class is a workflow that grabs the incoming request URI and
 * authorizes the current user (or current request if there is no user)
 * against that request URI.
 * </p>
 *
 * <p>
 * If the authorization fails, this class does one of two things based
 * on the type of failure.
 * </p>
 *
 * <h3>Not logged it</h3>
 * <p>
 * If the user is not logged in and needs to be, this class redirects
 * the request to the login URL. The login URL is setup using the
 * configuration parameter named <code>jcatapult.security.login.url</code>.
 * If this parameter is not set, the default is <code>/login</code>.
 * </p>
 *
 * <h3>Invalid permissions</h3>
 * <p>
 * If the user is logged in but doesn't have the correct permissions for
 * the request URI, this class redirects the request to the not authorized
 * URL. The not authorized URL is setup using the configuration parameter
 * named <code>jcatapult.security.not-authorized.url</code>.
 * If this parameter is not set, the default is <code>/not-authorized</code>.
 * </p>
 *
 * <p>
 * Whenever a failure occurs as described above, the workflow is stopped
 * completely because the HTTP response is a redirect and no more processing
 * should occur.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class AuthorizationWorkflow implements Workflow {
    private final Authorizer authorizer;
    private final String loginURL;
    private final String notAuthorizedURL;

    @Inject
    public AuthorizationWorkflow(Authorizer authorizer, Configuration configuration) {
        this.authorizer = authorizer;
        this.loginURL = configuration.getString("jcatapult.security.login.url", "/login");
        this.notAuthorizedURL = configuration.getString("jcatapult.security.not-authorized.url", "/not-authorized");
    }

    /**
     * Grabs the current request URI and then sends it along with the current user from the {@link SecurityContext}
     * to the {@link Authorizer} to be authorized.
     *
     * @param   request The HTTP request to get the request URI from.
     * @param   response Not used.
     * @param   workflowChain The workflow chain which is called if the authorization passes.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        Object user = SecurityContext.getCurrentUser();

        try {
            authorizer.authorize(user, uri);
        } catch (UnauthorizedException e) {
            httpResponse.sendRedirect(getContextPath(httpRequest, notAuthorizedURL));
            return;
        } catch (NotLoggedInException e) {
            httpResponse.sendRedirect(getContextPath(httpRequest, loginURL));
            return;
        }

        workflowChain.doWorkflow(request, response);
    }

    private String getContextPath(HttpServletRequest httpRequest, String url) {
        String context = httpRequest.getContextPath();
        if (context.equals("")) {
            return url;
        }

        if (url.startsWith("/")) {
            return context + url;
        }

        return context + "/" + url;
    }

    public void destroy() {
    }
}