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

import org.jcatapult.security.JCatapultSecurityException;
import org.jcatapult.security.config.SecurityConfiguration;
import org.jcatapult.security.login.LoginService;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This workflow checks the request for two specific parameters and
 * if they exist, they are used to login the user of the request. After
 * the user has logged in, processing continues as usual. If the
 * parameters don't exist, processing continues as normal.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class RestfulLoginWorkflow implements LoginWorkflow {
    private final HttpServletRequest request;
    private final LoginService loginService;
    private final LoginExceptionHandler exceptionHandler;
    private final String userNameParameter;
    private final String passwordParameter;

    @Inject
    public RestfulLoginWorkflow(HttpServletRequest request, LoginService loginService,
            SecurityConfiguration configuration, LoginExceptionHandler exceptionHandler) {
        this.request = request;
        this.loginService = loginService;
        this.exceptionHandler = exceptionHandler;
        this.userNameParameter = configuration.getUsernameParameter();
        this.passwordParameter = configuration.getPasswordParameter();
    }

    public void perform(WorkflowChain chain) throws IOException, ServletException {
        String username = request.getParameter(userNameParameter);
        String password = request.getParameter(passwordParameter);
        if (username != null || password != null) {
            try {
                loginService.login(username, password, request.getParameterMap());
                chain.continueWorkflow();
            } catch (JCatapultSecurityException e) {
                exceptionHandler.handle(e, chain);
            }
        }
    }

    public void destroy() {
    }
}