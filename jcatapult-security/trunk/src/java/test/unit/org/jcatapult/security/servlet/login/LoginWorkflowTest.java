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
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.login.InvalidUsernameException;
import org.jcatapult.security.login.LoginService;
import org.jcatapult.servlet.WorkflowChain;
import org.junit.Test;

/**
 * <p>
 * This tests the login workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class LoginWorkflowTest {
    @Test
    public void testIncorrectURI() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
        EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
        EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
        EasyMock.replay(c);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/not-login");
        EasyMock.replay(req);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        wc.doWorkflow(req, null);
        EasyMock.replay(wc);

        LoginWorkflow lw = new LoginWorkflow(null, c, null, null);
        lw.perform(req, null, wc);

        EasyMock.verify(c, req, wc);
    }

    @Test
    public void testSuccessfulLogin() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
        EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
        EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
        EasyMock.replay(c);

        Map<String, Object> params = new HashMap<String, Object>();
        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/jcatapult-security-check");
        EasyMock.expect(req.getParameter("j_username")).andReturn("test-username");
        EasyMock.expect(req.getParameter("j_password")).andReturn("test-password");
        EasyMock.expect(req.getParameterMap()).andReturn(params);
        EasyMock.replay(req);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(wc);

        Object user = new Object();
        LoginService ls = EasyMock.createStrictMock(LoginService.class);
        EasyMock.expect(ls.login("test-username", "test-password", params)).andReturn(user);
        EasyMock.replay(ls);

        PostLoginHandler plh = EasyMock.createStrictMock(PostLoginHandler.class);
        plh.handle(req, null, wc);
        EasyMock.replay(plh);

        LoginWorkflow lw = new LoginWorkflow(ls, c, null, plh);
        lw.perform(req, null, wc);

        EasyMock.verify(c, req, wc, ls, plh);
    }

    @Test
    public void testFailedLogin() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
        EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
        EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
        EasyMock.replay(c);

        Map<String, Object> params = new HashMap<String, Object>();
        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/jcatapult-security-check");
        EasyMock.expect(req.getParameter("j_username")).andReturn("test-username");
        EasyMock.expect(req.getParameter("j_password")).andReturn("test-password");
        EasyMock.expect(req.getParameterMap()).andReturn(params);
        EasyMock.replay(req);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(wc);

        InvalidUsernameException exception = new InvalidUsernameException();
        LoginService ls = EasyMock.createStrictMock(LoginService.class);
        EasyMock.expect(ls.login("test-username", "test-password", params)).andThrow(exception);
        EasyMock.replay(ls);

        LoginExceptionHandler leh = EasyMock.createStrictMock(LoginExceptionHandler.class);
        leh.handle(exception, req, null, wc);
        EasyMock.replay(leh);

        LoginWorkflow lw = new LoginWorkflow(ls, c, leh, null);
        lw.perform(req, null, wc);

        EasyMock.verify(c, req, wc, ls, leh);
    }
}