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

import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.auth.Authorizer;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.auth.UnauthorizedException;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

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
 * If the user is not logged in and needs to be, this class delegates
 * control to an implementation of the {@link NotLoggedInHandler}.
 * </p>
 *
 * <h3>Invalid permissions</h3>
 * <p>
 * If the user is logged in but doesn't have the correct permissions for
 * the request URI, this class delegates control to an implementation of
 * the {@link AuthorizationExceptionHandler}
 * </p>
 *
 * <p>
 * Whenever a failure occurs as described above, the workflow is not
 * executed so that those classes can take control. Otherwise, the workflow
 * chain is executed to continue processing the request.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class AuthorizationWorkflow implements Workflow {
    private final Authorizer authorizer;
    private final NotLoggedInHandler notLoggedInHandler;
    private final AuthorizationExceptionHandler authorizationExceptionHandler;

    @Inject
    public AuthorizationWorkflow(Authorizer authorizer, NotLoggedInHandler notLoggedInHandler,
            AuthorizationExceptionHandler authorizationExceptionHandler) {
        this.authorizer = authorizer;
        this.notLoggedInHandler = notLoggedInHandler;
        this.authorizationExceptionHandler = authorizationExceptionHandler;
    }

    /**
     * Grabs the current request URI and then sends it along with the current user from the
     * {@link org.jcatapult.security.EnhancedSecurityContext} to the {@link Authorizer} to be
     * authorized. If authorization fails, this delegates control to the
     * {@link AuthorizationExceptionHandler}. If there is not a logged in user and there must
     * be one to verify credentials (i.e. the authorizer threw a NotLoggedInException), this
     * delegates control to the {@link NotLoggedInHandler}.
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
        String uri = httpRequest.getRequestURI();
        Object user = EnhancedSecurityContext.getCurrentUser();

        try {
            authorizer.authorize(user, uri);
        } catch (UnauthorizedException e) {
            authorizationExceptionHandler.handle(e, request, response, workflowChain);
            return;
        } catch (NotLoggedInException e) {
            notLoggedInHandler.handle(e, request, response, workflowChain);
            return;
        }

        workflowChain.doWorkflow(request, response);
    }

    public void destroy() {
    }
}