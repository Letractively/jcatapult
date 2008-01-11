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
package org.jcatapult.security.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.security.AuthenticationService;
import org.jcatapult.security.InvalidLoginException;
import org.jcatapult.security.InvalidPasswordException;
import org.jcatapult.security.PasswordEncryptor;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class LoginWorkflow implements Workflow {
    private final AuthenticationService authenticationService;
    private final UserAdapter userAdapter;
    private final PasswordEncryptor passwordEncryptor;
    private final String loginURL;
    private final String userNameParameter;
    private final String passwordParameter;

    @Inject
    public LoginWorkflow(AuthenticationService authenticationService, UserAdapter userAdapter,
            PasswordEncryptor passwordEncryptor, Configuration configuration) {
        this.authenticationService = authenticationService;
        this.userAdapter = userAdapter;
        this.passwordEncryptor = passwordEncryptor;
        this.loginURL = configuration.getString("jcatapult.security.workflow.login.url", "/jcatapult_security_check");
        this.userNameParameter = configuration.getString("jcatapult.security.workflow.username.parameter", "j_username");
        this.passwordParameter = configuration.getString("jcatapult.security.workflow.password.parameter", "j_password");
    }

    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getRequestURI().equals(loginURL)) {
            String userName = request.getParameter(userNameParameter);
            String password = request.getParameter(passwordParameter);
            if (userName == null || password == null) {
                throw new ServletException("The login form must have a username and password field named " +
                    "[j_username] and [j_password] respectively.");
            }

            Object user = authenticationService.loadUser(userName, request.getParameterMap());
            if (user == null) {
                throw new InvalidLoginException();
            }

            String encrypted = passwordEncryptor.encryptPassword(password, user);
            String userPassword = userAdapter.getPassword(user);
            if (userPassword.equals(encrypted)) {
                throw new InvalidPasswordException();
            }
        }

        workflowChain.doWorkflow(request, response);
    }

    public void destroy() {
    }
}