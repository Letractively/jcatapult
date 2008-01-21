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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import org.jcatapult.security.auth.Authorizer;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.auth.UnauthorizedException;
import org.jcatapult.servlet.WorkflowChain;
import org.junit.Test;

/**
 * <p>
 * This tests the AuthorizationWorkflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class AuthorizationWorkflowTest {
    @Test
    public void testUnauthorized() throws IOException, ServletException {
        Object user = new Object();

        Authorizer a = EasyMock.createStrictMock(Authorizer.class);
        a.authorize(user, "/foo");
        EasyMock.expectLastCall().andThrow(new UnauthorizedException());
        EasyMock.replay(a);

        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.url", "/login")).andReturn("/login-url");
        EasyMock.expect(c.getString("jcatapult.security.authorization.restricted-url", "/not-authorized")).andReturn("/not-authed");
        EasyMock.replay(c);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/foo");
        EasyMock.expect(req.getContextPath()).andReturn("/context");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        res.sendRedirect("/context/not-authed");
        EasyMock.replay(res);

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));
        EnhancedSecurityContext.login(user);

        AuthorizationWorkflow aw = new AuthorizationWorkflow(a, c);
        aw.perform(req, res, null);
        EasyMock.verify(a, c, req, res);
    }

    @Test
    public void testNotLoggedIn() throws IOException, ServletException {
        Object user = new Object();

        Authorizer a = EasyMock.createStrictMock(Authorizer.class);
        a.authorize(user, "/foo");
        EasyMock.expectLastCall().andThrow(new NotLoggedInException());
        EasyMock.replay(a);

        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.url", "/login")).andReturn("/login-url");
        EasyMock.expect(c.getString("jcatapult.security.authorization.restricted-url", "/not-authorized")).andReturn("/not-authed");
        EasyMock.replay(c);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/foo");
        EasyMock.expect(req.getContextPath()).andReturn("");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        res.sendRedirect("/login-url");
        EasyMock.replay(res);

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));
        EnhancedSecurityContext.login(user);

        AuthorizationWorkflow aw = new AuthorizationWorkflow(a, c);
        aw.perform(req, res, null);
        EasyMock.verify(a, c, req, res);
    }

    @Test
    public void testSuccess() throws IOException, ServletException {
        Object user = new Object();

        Authorizer a = EasyMock.createStrictMock(Authorizer.class);
        a.authorize(user, "/foo");
        EasyMock.replay(a);

        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.url", "/login")).andReturn("/login-url");
        EasyMock.expect(c.getString("jcatapult.security.authorization.restricted-url", "/not-authorized")).andReturn("/not-authed");
        EasyMock.replay(c);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/foo");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        wc.doWorkflow(req, res);
        EasyMock.replay(wc);

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));
        EnhancedSecurityContext.login(user);

        AuthorizationWorkflow aw = new AuthorizationWorkflow(a, c);
        aw.perform(req, res, wc);
        EasyMock.verify(a, c, req, res);
    }
}