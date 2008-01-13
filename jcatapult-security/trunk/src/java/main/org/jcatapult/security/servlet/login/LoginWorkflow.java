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
import org.jcatapult.security.login.LoginService;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This workflow logs the user into an application if the incoming URI
 * is the correct URI and the username and password are correct for
 * the user.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class LoginWorkflow implements Workflow {
    private final LoginService loginService;
    private final LoginExceptionHandler exceptionHandler;
    private final PostLoginHandler loginHandler;
    private final String loginURI;
    private final String userNameParameter;
    private final String passwordParameter;

    @Inject
    public LoginWorkflow(LoginService loginService, Configuration configuration,
            LoginExceptionHandler exceptionHandler, PostLoginHandler loginHandler) {
        this.loginService = loginService;
        this.exceptionHandler = exceptionHandler;
        this.loginHandler = loginHandler;
        this.loginURI = configuration.getString("jcatapult.security.login.uri", "/jcatapult-security-check");
        this.userNameParameter = configuration.getString("jcatapult.security.login.username-parameter", "j_username");
        this.passwordParameter = configuration.getString("jcatapult.security.login.password-parameter", "j_password");
    }

    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI().equals(loginURI)) {
            String userName = request.getParameter(userNameParameter);
            String password = request.getParameter(passwordParameter);
            if (userName == null || password == null) {
                throw new ServletException("The login form must have a username and password field named " +
                    "[" + userNameParameter + "] and [" + passwordParameter + "] respectively.");
            }

            try {
                loginService.login(userName, password, request.getParameterMap());
                loginHandler.handle(request, response, workflowChain);
            } catch (JCatapultSecurityException e) {
                exceptionHandler.handle(e, request, response, workflowChain);
            }
        } else {
            workflowChain.doWorkflow(request, response);
        }
    }

    public void destroy() {
    }
}