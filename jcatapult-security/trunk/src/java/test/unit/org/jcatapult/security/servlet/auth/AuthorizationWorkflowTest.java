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

import org.easymock.EasyMock;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import org.jcatapult.security.auth.Authorizer;
import org.jcatapult.security.auth.NotLoggedInException;
import org.jcatapult.security.auth.AuthorizationException;
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

        AuthorizationException ue = new AuthorizationException();
        Authorizer a = EasyMock.createStrictMock(Authorizer.class);
        a.authorize(user, "/foo");
        EasyMock.expectLastCall().andThrow(ue);
        EasyMock.replay(a);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/foo");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));
        EnhancedSecurityContext.login(user);

        AuthorizationExceptionHandler aeh = EasyMock.createStrictMock(AuthorizationExceptionHandler.class);
        aeh.handle(ue, req, res, null);
        EasyMock.replay(aeh);

        AuthorizationWorkflow aw = new AuthorizationWorkflow(a, null, aeh);
        aw.perform(req, res, null);
        EasyMock.verify(a, req, res, aeh);
    }

    @Test
    public void testNotLoggedIn() throws IOException, ServletException {
        Object user = new Object();

        NotLoggedInException nlie = new NotLoggedInException();
        Authorizer a = EasyMock.createStrictMock(Authorizer.class);
        a.authorize(user, "/foo");
        EasyMock.expectLastCall().andThrow(nlie);
        EasyMock.replay(a);

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(req.getRequestURI()).andReturn("/foo");
        EasyMock.replay(req);

        HttpServletResponse res = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(res);

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));
        EnhancedSecurityContext.login(user);

        NotLoggedInHandler nlih = EasyMock.createStrictMock(NotLoggedInHandler.class);
        nlih.handle(nlie, req, res, null);
        EasyMock.replay(nlih);

        AuthorizationWorkflow aw = new AuthorizationWorkflow(a, nlih, null);
        aw.perform(req, res, null);
        EasyMock.verify(a, req, res, nlih);
    }

    @Test
    public void testSuccess() throws IOException, ServletException {
        Object user = new Object();

        Authorizer a = EasyMock.createStrictMock(Authorizer.class);
        a.authorize(user, "/foo");
        EasyMock.replay(a);

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

        AuthorizationWorkflow aw = new AuthorizationWorkflow(a, null, null);
        aw.perform(req, res, wc);
        EasyMock.verify(a, req, res);
    }
}