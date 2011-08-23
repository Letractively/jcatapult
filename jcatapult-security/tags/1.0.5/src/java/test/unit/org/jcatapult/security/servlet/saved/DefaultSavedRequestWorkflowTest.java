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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
import org.junit.Test;

import static net.java.util.CollectionTools.*;
import static net.java.util.Pair.*;
import static org.junit.Assert.*;

/**
 * <p> This tests the default login exception handler. </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultSavedRequestWorkflowTest {
  @Test
  public void testMainWorkflowNoSavedRequest() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
    EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
    EasyMock.replay(c);

    HttpSession session = EasyMock.createStrictMock(HttpSession.class);
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.POST_LOGIN_KEY)).andReturn(null);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(false)).andReturn(session);
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    EasyMock.replay(response);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    wc.continueWorkflow();
    EasyMock.replay(wc);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
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
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.POST_LOGIN_KEY)).andReturn(sr);
    session.removeAttribute(DefaultSavedRequestWorkflow.POST_LOGIN_KEY);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(false)).andReturn(session);
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

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
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

    SavedHttpRequest sr = new SavedHttpRequest("/context/foo", map(p("id", new String[]{"1"})));
    HttpSession session = EasyMock.createStrictMock(HttpSession.class);
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY)).andReturn(sr);
    session.removeAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY);
    session.setAttribute(DefaultSavedRequestWorkflow.POST_LOGIN_KEY, sr);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    response.sendRedirect("/context/foo");
    EasyMock.replay(response);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
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
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY)).andReturn(null);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.expect(request.getContextPath()).andReturn("");
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

      public void reset() {
        fail("Should not be called");
      }
    };

    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
    ServletObjectsHolder.clearServletRequest();
    ServletObjectsHolder.setServletRequest(wrapper);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
    srw.handle(wc);
    assertTrue(called.get());
    EasyMock.verify(c, request, response, session);
  }

  @Test
  public void testPostLoginHandleWithNoSavedRequestContext() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
    EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
    EasyMock.replay(c);

    HttpSession session = EasyMock.createStrictMock(HttpSession.class);
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY)).andReturn(null);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.expect(request.getContextPath()).andReturn("/context");
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    EasyMock.replay(response);

    final AtomicBoolean called = new AtomicBoolean(false);
    WorkflowChain wc = new WorkflowChain() {
      public void continueWorkflow() throws IOException, ServletException {
        assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
        assertEquals("/context/login-success", ServletObjectsHolder.getServletRequest().getRequestURI());
        called.set(true);
      }

      public void reset() {
        fail("Should not be called");
      }
    };

    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
    ServletObjectsHolder.clearServletRequest();
    ServletObjectsHolder.setServletRequest(wrapper);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
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
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY)).andReturn(sr);
    session.removeAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    response.sendRedirect("/foo");
    EasyMock.replay(response);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
    srw.handle(null);
    EasyMock.verify(c, request, response, session);
  }

  @Test
  public void testPostLoginHandleWithSavedRequestAfterGetContext() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
    EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
    EasyMock.replay(c);

    SavedHttpRequest sr = new SavedHttpRequest("/context/foo", null);
    HttpSession session = EasyMock.createStrictMock(HttpSession.class);
    EasyMock.expect(session.getAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY)).andReturn(sr);
    session.removeAttribute(DefaultSavedRequestWorkflow.LOGIN_KEY);
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    response.sendRedirect("/context/foo");
    EasyMock.replay(response);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(new HttpServletRequestWrapper(request), response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
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
    session.setAttribute(EasyMock.eq(DefaultSavedRequestWorkflow.LOGIN_KEY), EasyMock.eq(new SavedHttpRequest("/foo/bar?id=1", null)));
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getMethod()).andReturn("GET");
    Map<String, String[]> params = map(p("id", new String[]{"1"}));
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.expect(request.getRequestURL()).andReturn(new StringBuffer("http://www.example.com/foo/bar"));
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.expect(request.getContextPath()).andReturn("");
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

      public void reset() {
        fail("Should not be called");
      }
    };

    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
    ServletObjectsHolder.clearServletRequest();
    ServletObjectsHolder.setServletRequest(wrapper);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
    srw.handle(new NotLoggedInException(), wc);
    EasyMock.verify(c, request, response, session);
    assertTrue(called.get());
  }

  @Test
  public void testNotLoggedInHandleContext() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
    EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
    EasyMock.replay(c);

    HttpSession session = EasyMock.createStrictMock(HttpSession.class);
    session.setAttribute(EasyMock.eq(DefaultSavedRequestWorkflow.LOGIN_KEY), EasyMock.eq(new SavedHttpRequest("/context/foo/bar?id=1", null)));
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getMethod()).andReturn("GET");
    Map<String, String[]> params = map(p("id", new String[]{"1"}));
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.expect(request.getRequestURL()).andReturn(new StringBuffer("http://www.example.com/context/foo/bar"));
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.expect(request.getContextPath()).andReturn("/context");
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    EasyMock.replay(response);

    final AtomicBoolean called = new AtomicBoolean(false);
    WorkflowChain wc = new WorkflowChain() {
      public void continueWorkflow() throws IOException, ServletException {
        assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
        assertEquals("/context/not-logged-in", ServletObjectsHolder.getServletRequest().getRequestURI());
        called.set(true);
      }

      public void reset() {
        fail("Should not be called");
      }
    };

    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
    ServletObjectsHolder.clearServletRequest();
    ServletObjectsHolder.setServletRequest(wrapper);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
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
    session.setAttribute(EasyMock.eq(DefaultSavedRequestWorkflow.LOGIN_KEY), EasyMock.eq(new SavedHttpRequest("/foo/bar", params)));
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.expect(request.getRequestURI()).andReturn("/foo/bar");
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.expect(request.getContextPath()).andReturn("");
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

      public void reset() {
        fail("Should not be called");
      }
    };

    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
    ServletObjectsHolder.clearServletRequest();
    ServletObjectsHolder.setServletRequest(wrapper);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
    srw.handle(new NotLoggedInException(), wc);
    EasyMock.verify(c, request, response, session);
    assertTrue(called.get());
  }

  @Test
  public void testNotLoggedInHandlePostContext() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.authorization.not-logged-in-uri", "/not-logged-in")).andReturn("/not-logged-in");
    EasyMock.expect(c.getString("jcatapult.security.login.success-uri", "/login-success")).andReturn("/login-success");
    EasyMock.replay(c);

    Map<String, String[]> params = map(p("id", new String[]{"1"}));
    HttpSession session = EasyMock.createStrictMock(HttpSession.class);
    session.setAttribute(EasyMock.eq(DefaultSavedRequestWorkflow.LOGIN_KEY), EasyMock.eq(new SavedHttpRequest("/context/foo/bar", params)));
    EasyMock.replay(session);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.expect(request.getRequestURI()).andReturn("/context/foo/bar");
    EasyMock.expect(request.getSession(true)).andReturn(session);
    EasyMock.expect(request.getContextPath()).andReturn("/context");
    EasyMock.replay(request);

    HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
    EasyMock.replay(response);

    final AtomicBoolean called = new AtomicBoolean(false);
    WorkflowChain wc = new WorkflowChain() {
      public void continueWorkflow() throws IOException, ServletException {
        assertTrue(ServletObjectsHolder.getServletRequest().getRequest() instanceof FacadeHttpServletRequest);
        assertEquals("/context/not-logged-in", ServletObjectsHolder.getServletRequest().getRequestURI());
        called.set(true);
      }

      public void reset() {
        fail("Should not be called");
      }
    };

    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
    ServletObjectsHolder.clearServletRequest();
    ServletObjectsHolder.setServletRequest(wrapper);

    DefaultSavedRequestWorkflow srw = new DefaultSavedRequestWorkflow(wrapper, response, new DefaultSecurityConfiguration(c), new DefaultSavedRequestService());
    srw.handle(new NotLoggedInException(), wc);
    EasyMock.verify(c, request, response, session);
    assertTrue(called.get());
  }
}
