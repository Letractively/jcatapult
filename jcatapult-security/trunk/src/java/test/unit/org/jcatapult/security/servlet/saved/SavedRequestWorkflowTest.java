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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.servlet.FacadeHttpServletRequest;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
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
public class SavedRequestWorkflowTest {
    @Test
    public void testMainWorkflowNoSavedRequest() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.page-uri", "/login")).andReturn("/login");
        EasyMock.expect(c.getString("jcatapult.security.login.successful-login-uri", "/successful-login")).andReturn("/successful-login");
        EasyMock.replay(c);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.POST_LOGIN_KEY)).andReturn(null);
        EasyMock.replay(session);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getSession(true)).andReturn(session);
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        wc.doWorkflow(req, res);
        EasyMock.replay(wc);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(c);
        srw.perform(req, res, wc);
        EasyMock.verify(c, req, res);
    }

    @Test
    public void testMainWorkflowSavedRequest() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.page-uri", "/login")).andReturn("/login");
        EasyMock.expect(c.getString("jcatapult.security.login.successful-login-uri", "/successful-login")).andReturn("/successful-login");
        EasyMock.replay(c);

        SavedHttpRequest sr = new SavedHttpRequest(null, new HashMap<String, String[]>());
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.POST_LOGIN_KEY)).andReturn(sr);
        session.removeAttribute(SavedRequestWorkflow.POST_LOGIN_KEY);
        EasyMock.replay(session);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getSession(true)).andReturn(session);
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        wc.doWorkflow(EasyMock.isA(FacadeHttpServletRequest.class), EasyMock.isA(HttpServletResponse.class));
        EasyMock.replay(wc);

        JCatapultSecurityContextProvider provider = new JCatapultSecurityContextProvider(null);
        provider.login(new Object());
        SecurityContext.setProvider(provider);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(c);
        srw.perform(req, res, wc);
        EasyMock.verify(c, req, res);
    }

    @Test
    public void testPostLoginHandleWithNoSavedRequest() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.page-uri", "/login")).andReturn("/login");
        EasyMock.expect(c.getString("jcatapult.security.login.successful-login-uri", "/successful-login")).andReturn("/successful-login");
        EasyMock.replay(c);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.LOGIN_KEY)).andReturn(null);
        EasyMock.replay(session);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getSession(true)).andReturn(session);
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void doWorkflow(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                assertNotNull(request);
                assertNotNull(response);
                assertTrue(request instanceof FacadeHttpServletRequest);
                assertEquals("/successful-login", ((HttpServletRequest) request).getRequestURI());
                called.set(true);
            }
        };

        SavedRequestWorkflow srw = new SavedRequestWorkflow(c);
        srw.handle(req, res, wc);
        assertTrue(called.get());
        EasyMock.verify(c, req, res);
    }

    @Test
    public void testPostLoginHandleWithSavedRequest() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.page-uri", "/login")).andReturn("/login");
        EasyMock.expect(c.getString("jcatapult.security.login.successful-login-uri", "/successful-login")).andReturn("/successful-login");
        EasyMock.replay(c);

        SavedHttpRequest sr = new SavedHttpRequest("/foo", new HashMap<String, String[]>());
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.LOGIN_KEY)).andReturn(sr);
        session.removeAttribute(SavedRequestWorkflow.LOGIN_KEY);
        session.setAttribute(SavedRequestWorkflow.POST_LOGIN_KEY, sr);
        EasyMock.replay(session);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getSession(true)).andReturn(session);
        EasyMock.expect(req.getContextPath()).andReturn("/context");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        res.sendRedirect("/context/foo");
        EasyMock.replay(res);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(c);
        srw.handle(req, res, null);
        EasyMock.verify(c, req, res);
    }

    @Test
    public void testNotLoggedInHandle() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.login.page-uri", "/login")).andReturn("/login");
        EasyMock.expect(c.getString("jcatapult.security.login.successful-login-uri", "/successful-login")).andReturn("/successful-login");
        EasyMock.replay(c);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        session.setAttribute(EasyMock.eq(SavedRequestWorkflow.LOGIN_KEY), EasyMock.isA(SavedHttpRequest.class));
        EasyMock.replay(session);

        Map<String, String[]> params = new HashMap<String, String[]>();
        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getSession(true)).andReturn(session);
        EasyMock.expect(req.getRequestURI()).andReturn("/foo");
        EasyMock.expect(req.getParameterMap()).andReturn(params);
        EasyMock.expect(req.getContextPath()).andReturn("");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        res.sendRedirect("/login");
        EasyMock.replay(res);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(c);
        srw.handle(new NotLoggedInException(), req, res, null);
        EasyMock.verify(c, req, res);
    }
}