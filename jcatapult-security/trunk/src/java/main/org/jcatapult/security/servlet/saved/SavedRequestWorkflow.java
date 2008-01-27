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
package org.jcatapult.security.servlet.saved;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.servlet.FacadeHttpServletRequest;
import static org.jcatapult.security.servlet.ServletTools.*;
import org.jcatapult.security.servlet.auth.NotLoggedInHandler;
import org.jcatapult.security.servlet.login.PostLoginHandler;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class provides saved request handling logic by implementing
 * the {@link PostLoginHandler} and {@link NotLoggedInHandler}.
 * </p>
 *
 * <h3>Login handling</h3>
 * <p>
 * This class handles authorization failures due to no user being logged
 * into the application. In this case, this handler saves the request into
 * the session so that once the user logs in the request can be re-executed.
 * Once the request is saved, this class redirects to the login URI. This
 * URI is set using the configuration parameter named
 * <code>jcatapult.security.login.uri</code>. This parameter defaults to
 * <code>/login</code>.
 * </p>
 *
 * <h3>Post login handling</h3>
 * <p>
 * This class handles successful logins by first checking for a saved
 * request. If there is a saved request, it is re-executed using a
 * redirect. If there is no saved request, this class passes the control
 * to a struts action. The URI of the struts action that is invoked is
 * controlled by the configuratin parameter
 * <code>jcatapult.security.login.succesful-login-uri</code>. This parameter
 * defaults to <b>/successful-login</b>.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class SavedRequestWorkflow implements PostLoginHandler, NotLoggedInHandler, Workflow {
    public static final String LOGIN_KEY = "org.jcatapult.security.servlet.saved.loginSavedHttpRequest";
    public static final String POST_LOGIN_KEY = "org.jcatapult.security.servlet.saved.postLoginSavedHttpRequest";
    private final String loginURL;
    private final String successfulLoginURI;

    @Inject
    public SavedRequestWorkflow(Configuration configuration) {
        this.loginURL = configuration.getString("jcatapult.security.login.page-uri", "/login");
        this.successfulLoginURI = configuration.getString("jcatapult.security.login.successful-login-uri", "/successful-login");
    }

    // --------------------- Workflow methods ----------------------

    /**
     * During a redirect to a saved request, this will pull the saved request from the session and
     * mock out a request so that stored parameters can be used.
     *
     * @param   request The incoming request from the servlet container.
     * @param   response The response.
     * @param   workflowChain Once the request is mocked or not, this is invoked.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        // See if there is a saved request
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(true);
        SavedHttpRequest saved = (SavedHttpRequest) session.getAttribute(POST_LOGIN_KEY);
        if (saved != null && SecurityContext.getCurrentUser() != null) {
            session.removeAttribute(POST_LOGIN_KEY);
            httpRequest = new FacadeHttpServletRequest(httpRequest, null, saved.parameters);
        }

        workflowChain.doWorkflow(httpRequest, response);
    }

    /**
     * Empty.
     */
    public void destroy() {
    }

    // --------------------- PostLoginHandler methods ----------------------

    /**
     * Handles post login and also the saved request if one exists. If the saved request exists, a
     * redirect is sent to the saved requests URI. Otherwise a a facade is created to invoke the
     * Struts action of the successful login URI.
     *
     * @param   request The request used to get the session and check for saved requests.
     * @param   response The response used for directs.
     * @param   workflowChain THe workflow change that is called if no direct
     * @throws  ServletException If the chain throws.
     * @throws  IOException If the chain throws.
     */
    public void handle(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws ServletException, IOException {
        // See if there is a saved request
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        SavedHttpRequest saved = (SavedHttpRequest) session.getAttribute(LOGIN_KEY);
        if (saved != null) {
            session.removeAttribute(LOGIN_KEY);
            session.setAttribute(POST_LOGIN_KEY, saved);
            httpResponse.sendRedirect(getContextURI(httpRequest, saved.uri));
        } else {
            FacadeHttpServletRequest facade = new FacadeHttpServletRequest(httpRequest, successfulLoginURI, null);
            workflowChain.doWorkflow(facade, response);
        }
    }

    // --------------------- NotLoggedInHandler methods ----------------------

    /**
     * Handles authorization failures when there is no user logged in. This method saves the request
     * into the session so that it can be re-executed.
     *
     * @param   exception The not logged in exception that was thrown.
     * @param   request The request used to get the session for saving the request.
     * @param   response The response used for the direct to the login page.
     * @param   workflowChain The chain is not used.
     * @throws  ServletException If the redirect throws.
     * @throws  IOException If the redirect throws.
     */
    public void handle(NotLoggedInException exception, ServletRequest request, ServletResponse response,
            WorkflowChain workflowChain)
    throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);

        // Save the request
        String uri = httpRequest.getRequestURI();
        Map<String, String[]> requestParameters = httpRequest.getParameterMap();
        SavedHttpRequest saved = new SavedHttpRequest(uri, requestParameters);
        session.setAttribute(LOGIN_KEY, saved);

        httpResponse.sendRedirect(getContextURI(httpRequest, loginURL));
    }

}