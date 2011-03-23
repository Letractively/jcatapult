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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.config.DefaultSecurityConfiguration;
import org.jcatapult.security.login.InvalidUsernameException;
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
public class DefaultLoginExceptionHandlerTest {
    @Test
    public void testHandle() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.failed-uri", "/login-failed")).andReturn("/login-failed");
        EasyMock.replay(c);

        InvalidUsernameException exception = new InvalidUsernameException();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getContextPath()).andReturn("");
        request.setAttribute("jcatapult_security_login_exception", exception);
        EasyMock.replay(request);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() throws IOException, ServletException {
                assertTrue(ServletObjectsHolder.getServletRequest() instanceof HttpServletRequestWrapper);
                assertEquals("/login-failed", ServletObjectsHolder.getServletRequest().getRequestURI());
                called.set(true);
            }

            public void reset() {
                fail("Should not be called");
            }
        };

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        DefaultLoginExceptionHandler dleh = new DefaultLoginExceptionHandler(wrapper, new DefaultSecurityConfiguration(c));
        dleh.handle(exception, wc);
        assertTrue(called.get());
        EasyMock.verify(c, request);
    }

    @Test
    public void testHandleContext() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.failed-uri", "/login-failed")).andReturn("/login-failed");
        EasyMock.replay(c);

        InvalidUsernameException exception = new InvalidUsernameException();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getContextPath()).andReturn("/context");
        request.setAttribute("jcatapult_security_login_exception", exception);
        EasyMock.replay(request);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() throws IOException, ServletException {
                assertTrue(ServletObjectsHolder.getServletRequest() instanceof HttpServletRequestWrapper);
                assertEquals("/context/login-failed", ServletObjectsHolder.getServletRequest().getRequestURI());
                called.set(true);
            }

            public void reset() {
                fail("Should not be called");
            }
        };

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        DefaultLoginExceptionHandler dleh = new DefaultLoginExceptionHandler(wrapper, new DefaultSecurityConfiguration(c));
        dleh.handle(exception, wc);
        assertTrue(called.get());
        EasyMock.verify(c, request);
    }
}
