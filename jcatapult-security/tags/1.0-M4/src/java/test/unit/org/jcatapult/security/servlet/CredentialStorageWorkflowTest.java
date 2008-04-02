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
package org.jcatapult.security.servlet;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.servlet.WorkflowChain;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This tests the credential storage workflow.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class CredentialStorageWorkflowTest {
    @Test
    public void testExisting() throws IOException, ServletException {
        Object user = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(req);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(req)).andReturn(user);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void doWorkflow(ServletRequest request, ServletResponse response) {
                assertNotNull(SecurityContext.getCurrentUser());
                called.set(true);
            }
        };

        CredentialStorageWorkflow csw = new CredentialStorageWorkflow(cs);
        csw.perform(req, null, wc);
        assertNull(SecurityContext.getCurrentUser());
        assertTrue(called.get());
        EasyMock.verify(req, cs);
    }

    @Test
    public void testLogin() throws IOException, ServletException {
        final Object user = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(req);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(req)).andReturn(null);
        cs.store(user, req);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void doWorkflow(ServletRequest request, ServletResponse response) {
                assertNull(SecurityContext.getCurrentUser());
                EnhancedSecurityContext.login(user);
                called.set(true);
            }
        };

        CredentialStorageWorkflow csw = new CredentialStorageWorkflow(cs);
        csw.perform(req, null, wc);
        assertNull(SecurityContext.getCurrentUser());
        assertTrue(called.get());
        EasyMock.verify(req, cs);
    }

    @Test
    public void testLogout() throws IOException, ServletException {
        final Object user = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest req = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(req);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(req)).andReturn(user);
        cs.remove(req);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void doWorkflow(ServletRequest request, ServletResponse response) {
                assertNotNull(EnhancedSecurityContext.getCurrentUser());
                EnhancedSecurityContext.logout();
                called.set(true);
            }
        };

        CredentialStorageWorkflow csw = new CredentialStorageWorkflow(cs);
        csw.perform(req, null, wc);
        assertNull(EnhancedSecurityContext.getCurrentUser());
        assertTrue(called.get());
        EasyMock.verify(req, cs);
    }
}