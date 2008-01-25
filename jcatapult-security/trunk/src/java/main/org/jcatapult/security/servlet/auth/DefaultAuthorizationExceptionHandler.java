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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.security.auth.UnauthorizedException;
import static org.jcatapult.security.servlet.ServletTools.getContextURI;
import org.jcatapult.servlet.WorkflowChain;
import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultAuthorizationExceptionHandler implements AuthorizationExceptionHandler {
    private final String notAuthorizedURL;

    @Inject
    public DefaultAuthorizationExceptionHandler(Configuration configuration) {
        this.notAuthorizedURL = configuration.getString("jcatapult.security.authorization.restricted-url", "/not-authorized");
    }

    public void handle(UnauthorizedException exception, ServletRequest request, ServletResponse response,
        WorkflowChain workflowChain)
    throws ServletException, IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        httpResponse.sendRedirect(getContextURI(httpRequest, notAuthorizedURL));
    }
}