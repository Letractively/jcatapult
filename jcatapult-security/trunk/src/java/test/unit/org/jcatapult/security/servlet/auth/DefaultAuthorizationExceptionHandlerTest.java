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
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.auth.AuthorizationException;
import org.jcatapult.security.config.DefaultSecurityConfiguration;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.servlet.WorkflowChain;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This tests the default login exception handler.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultAuthorizationExceptionHandlerTest {
    @Test
    public void testHandle() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.restricted-uri", "/not-authorized")).andReturn("/not-authorized");
        EasyMock.replay(c);

        AuthorizationException exception = new AuthorizationException();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        request.setAttribute("jcatapult_authorization_exception", exception);
        EasyMock.replay(request);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() throws IOException, ServletException {
                assertTrue(ServletObjectsHolder.getServletRequest() instanceof HttpServletRequestWrapper);
                assertEquals("/not-authorized", ServletObjectsHolder.getServletRequest().getRequestURI());
                called.set(true);
            }
        };

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        DefaultAuthorizationExceptionHandler dleh = new DefaultAuthorizationExceptionHandler(
            wrapper, new DefaultSecurityConfiguration(c));
        dleh.handle(exception, wc);
        assertTrue(called.get());
        EasyMock.verify(c, request);
    }
}