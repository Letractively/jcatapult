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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.config.DefaultSecurityConfiguration;
import org.jcatapult.security.saved.DefaultSavedRequestService;
import org.jcatapult.security.saved.SavedHttpRequest;
import org.jcatapult.security.servlet.FacadeHttpServletRequest;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.servlet.WorkflowChain;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;
import static net.java.util.Pair.*;

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
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.POST_LOGIN_KEY)).andReturn(null);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        wc.continueWorkflow();
        EasyMock.replay(wc);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.perform(wc);
        EasyMock.verify(c, request, response, session);
    }

    @Test
    public void testMainWorkflowSavedRequest() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        SavedHttpRequest sr = new SavedHttpRequest(null, new HashMap<String, String[]>());
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.POST_LOGIN_KEY)).andReturn(sr);
        session.removeAttribute(SavedRequestWorkflow.POST_LOGIN_KEY);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
        wc.continueWorkflow();
        EasyMock.replay(wc);

        JCatapultSecurityContextProvider provider = new JCatapultSecurityContextProvider(null);
        provider.login(new Object());
        SecurityContext.setProvider(provider);

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.perform(wc);
        assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
        EasyMock.verify(c, request, response, session);
    }

    @Test
    public void testPostLoginHandleWithSavedRequestAfterPost() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        SavedHttpRequest sr = new SavedHttpRequest("/foo", map(p("id", new String[]{"1"})));
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.LOGIN_KEY)).andReturn(sr);
        session.removeAttribute(SavedRequestWorkflow.LOGIN_KEY);
        session.setAttribute(SavedRequestWorkflow.POST_LOGIN_KEY, sr);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.expect(request.getContextPath()).andReturn("/context");
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.sendRedirect("/context/foo");
        EasyMock.replay(response);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.handle(null);
        EasyMock.verify(c, request, response, session);
    }

    @Test
    public void testPostLoginHandleWithNoSavedRequest() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.LOGIN_KEY)).andReturn(null);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() throws IOException, ServletException {
                assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
                assertEquals("/login-success", ServletObjectsHolder.getServletRequest().getRequestURI());
                called.set(true);
            }
        };

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.handle(wc);
        assertTrue(called.get());
        EasyMock.verify(c, request, response, session);
    }

    @Test
    public void testPostLoginHandleWithSavedRequestAfterGet() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        SavedHttpRequest sr = new SavedHttpRequest("/foo", null);
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute(SavedRequestWorkflow.LOGIN_KEY)).andReturn(sr);
        session.removeAttribute(SavedRequestWorkflow.LOGIN_KEY);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.expect(request.getContextPath()).andReturn("/context");
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.sendRedirect("/context/foo");
        EasyMock.replay(response);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.handle(null);
        EasyMock.verify(c, request, response, session);
    }

    @Test
    public void testNotLoggedInHandle() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        session.setAttribute(EasyMock.eq(SavedRequestWorkflow.LOGIN_KEY), EasyMock.eq(new SavedHttpRequest("/foo/bar?id=1", null)));
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        Map<String, String[]> params = map(p("id", new String[]{"1"}));
        EasyMock.expect(request.getParameterMap()).andReturn(params);
        EasyMock.expect(request.getRequestURL()).andReturn(new StringBuffer("http://www.example.com/foo/bar"));
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() throws IOException, ServletException {
                assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
                assertEquals("/not-logged-in", ServletObjectsHolder.getServletRequest().getRequestURI());
                called.set(true);
            }
        };

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.handle(new NotLoggedInException(), wc);
        EasyMock.verify(c, request, response, session);
        assertTrue(called.get());
    }

    @Test
    public void testNotLoggedInHandlePost() throws IOException, ServletException {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
        EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
        EasyMock.replay(c);

        Map<String, String[]> params = map(p("id", new String[]{"1"}));
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        session.setAttribute(EasyMock.eq(SavedRequestWorkflow.LOGIN_KEY), EasyMock.eq(new SavedHttpRequest("/foo/bar", params)));
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getParameterMap()).andReturn(params);
        EasyMock.expect(request.getRequestURI()).andReturn("/foo/bar");
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() throws IOException, ServletException {
                assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
                assertEquals("/not-logged-in", ServletObjectsHolder.getServletRequest().getRequestURI());
                called.set(true);
            }
        };

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(wrapper);

        SavedRequestWorkflow srw = new SavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
        srw.handle(new NotLoggedInException(), wc);
        EasyMock.verify(c, request, response, session);
        assertTrue(called.get());
    }
}