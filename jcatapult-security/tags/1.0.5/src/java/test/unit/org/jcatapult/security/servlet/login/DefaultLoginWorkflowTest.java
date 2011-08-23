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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.config.DefaultSecurityConfiguration;
import org.jcatapult.security.login.InvalidUsernameException;
import org.jcatapult.security.login.LoginService;
import org.jcatapult.servlet.WorkflowChain;
import org.junit.Test;

/**
 * This tests the login workflow.
 *
 * @author Brian Pontarelli
 */
public class DefaultLoginWorkflowTest {
  @Test
  public void incorrectURI() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn(null);
    EasyMock.expect(request.getParameter("j_password")).andReturn(null);
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getRequestURI()).andReturn("/not-login");
    EasyMock.expect(request.getContextPath()).andReturn("");
    EasyMock.replay(request);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    wc.continueWorkflow();
    EasyMock.replay(wc);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, null, new DefaultSecurityConfiguration(c), null, null);
    lw.perform(wc);

    EasyMock.verify(c, request, wc);
  }

  @Test
  public void incorrectMethod() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn(null);
    EasyMock.expect(request.getParameter("j_password")).andReturn(null);
    EasyMock.expect(request.getMethod()).andReturn("GET");
    EasyMock.expect(request.getRequestURI()).andReturn("/jcatapult-security-check");
    EasyMock.expect(request.getContextPath()).andReturn("");
    EasyMock.replay(request);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    wc.continueWorkflow();
    EasyMock.replay(wc);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, null, new DefaultSecurityConfiguration(c), null, null);
    lw.perform(wc);

    EasyMock.verify(c, request, wc);
  }

  @Test
  public void successfulLogin() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    Map<String, Object> params = new HashMap<String, Object>();
    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn("test-username");
    EasyMock.expect(request.getParameter("j_password")).andReturn("test-password");
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getRequestURI()).andReturn("/jcatapult-security-check");
    EasyMock.expect(request.getContextPath()).andReturn("");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.replay(request);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    EasyMock.replay(wc);

    Object user = new Object();
    LoginService ls = EasyMock.createStrictMock(LoginService.class);
    EasyMock.expect(ls.login("test-username", "test-password", params)).andReturn(user);
    EasyMock.replay(ls);

    PostLoginHandler plh = EasyMock.createStrictMock(PostLoginHandler.class);
    plh.handle(wc);
    EasyMock.replay(plh);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, ls, new DefaultSecurityConfiguration(c), null, plh);
    lw.perform(wc);

    EasyMock.verify(c, request, wc, ls, plh);
  }

  @Test
  public void successfulLoginContext() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    Map<String, Object> params = new HashMap<String, Object>();
    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn("test-username");
    EasyMock.expect(request.getParameter("j_password")).andReturn("test-password");
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getRequestURI()).andReturn("/context/jcatapult-security-check");
    EasyMock.expect(request.getContextPath()).andReturn("/context");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.replay(request);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    EasyMock.replay(wc);

    Object user = new Object();
    LoginService ls = EasyMock.createStrictMock(LoginService.class);
    EasyMock.expect(ls.login("test-username", "test-password", params)).andReturn(user);
    EasyMock.replay(ls);

    PostLoginHandler plh = EasyMock.createStrictMock(PostLoginHandler.class);
    plh.handle(wc);
    EasyMock.replay(plh);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, ls, new DefaultSecurityConfiguration(c), null, plh);
    lw.perform(wc);

    EasyMock.verify(c, request, wc, ls, plh);
  }

  @Test
  public void failedLogin() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    Map<String, Object> params = new HashMap<String, Object>();
    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn("test-username");
    EasyMock.expect(request.getParameter("j_password")).andReturn("test-password");
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getRequestURI()).andReturn("/jcatapult-security-check");
    EasyMock.expect(request.getContextPath()).andReturn("");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.replay(request);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    EasyMock.replay(wc);

    InvalidUsernameException exception = new InvalidUsernameException();
    LoginService ls = EasyMock.createStrictMock(LoginService.class);
    EasyMock.expect(ls.login("test-username", "test-password", params)).andThrow(exception);
    EasyMock.replay(ls);

    LoginExceptionHandler leh = EasyMock.createStrictMock(LoginExceptionHandler.class);
    leh.handle(exception, wc);
    EasyMock.replay(leh);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, ls, new DefaultSecurityConfiguration(c), leh, null);
    lw.perform(wc);

    EasyMock.verify(c, request, wc, ls, leh);
  }

  @Test
  public void restfulSuccess() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    // Login should still occur even if the URI is invalid but the username, password and method are all valid
    Map<String, Object> params = new HashMap<String, Object>();
    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn("test-username");
    EasyMock.expect(request.getParameter("j_password")).andReturn("test-password");
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getRequestURI()).andReturn("/not-login");
    EasyMock.expect(request.getContextPath()).andReturn("");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.replay(request);

    Object user = new Object();
    LoginService ls = EasyMock.createStrictMock(LoginService.class);
    EasyMock.expect(ls.login("test-username", "test-password", params)).andReturn(user);
    EasyMock.replay(ls);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    wc.continueWorkflow();
    EasyMock.replay(wc);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, ls, new DefaultSecurityConfiguration(c), null, null);
    lw.perform(wc);

    EasyMock.verify(c, request, wc, ls);
  }

  @Test
  public void restfulFailure() throws IOException, ServletException {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getString("jcatapult.security.login.submit-uri", "/jcatapult-security-check")).andReturn("/jcatapult-security-check");
    EasyMock.expect(c.getString("jcatapult.security.login.username-parameter", "j_username")).andReturn("j_username");
    EasyMock.expect(c.getString("jcatapult.security.login.password-parameter", "j_password")).andReturn("j_password");
    EasyMock.replay(c);

    // Login should still occur even if the URI is invalid but the username, password and method are all valid
    Map<String, Object> params = new HashMap<String, Object>();
    HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
    EasyMock.expect(request.getParameter("j_username")).andReturn("test-username");
    EasyMock.expect(request.getParameter("j_password")).andReturn("test-password");
    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock.expect(request.getRequestURI()).andReturn("/not-login");
    EasyMock.expect(request.getContextPath()).andReturn("");
    EasyMock.expect(request.getParameterMap()).andReturn(params);
    EasyMock.replay(request);

    WorkflowChain wc = EasyMock.createStrictMock(WorkflowChain.class);
    EasyMock.replay(wc);

    InvalidUsernameException exception = new InvalidUsernameException();
    LoginService ls = EasyMock.createStrictMock(LoginService.class);
    EasyMock.expect(ls.login("test-username", "test-password", params)).andThrow(exception);
    EasyMock.replay(ls);

    LoginExceptionHandler leh = EasyMock.createStrictMock(LoginExceptionHandler.class);
    leh.handle(exception, wc);
    EasyMock.replay(leh);

    DefaultLoginWorkflow lw = new DefaultLoginWorkflow(request, ls, new DefaultSecurityConfiguration(c), leh, null);
    lw.perform(wc);

    EasyMock.verify(c, request, wc, ls, leh);
  }
}