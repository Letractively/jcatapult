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
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.login.InvalidUsernameException;
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
        EasyMock.expect(c.getString("jcatapult.security.login.failed-login-uri", "/failed-login")).andReturn("/failed-login");
        EasyMock.replay(c);

        InvalidUsernameException exception = new InvalidUsernameException();

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        req.setAttribute("jcatapult_security_login_exception", exception);
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void doWorkflow(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                assertNotNull(request);
                assertNotNull(response);
                assertTrue(request instanceof HttpServletRequestWrapper);
                assertEquals("/failed-login", ((HttpServletRequest) request).getRequestURI());
                called.set(true);
            }
        };

        DefaultLoginExceptionHandler dleh = new DefaultLoginExceptionHandler(c);
        dleh.handle(exception, req, res, wc);
        assertTrue(called.get());
        EasyMock.verify(c, req, res);
    }
}