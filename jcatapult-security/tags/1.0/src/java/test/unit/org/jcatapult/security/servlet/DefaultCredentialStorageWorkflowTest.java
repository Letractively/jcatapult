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
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.SecurityContext;
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
public class DefaultCredentialStorageWorkflowTest {
    @Test
    public void testExisting() throws IOException, ServletException {
        Object user = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(request);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(request)).andReturn(user);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() {
                assertNotNull(SecurityContext.getCurrentUser());
                called.set(true);
            }
        };

        DefaultCredentialStorageWorkflow csw = new DefaultCredentialStorageWorkflow(request, cs);
        csw.perform(wc);
        assertTrue(called.get());
        EasyMock.verify(request, cs);
    }

    @Test
    public void testLogin() throws IOException, ServletException {
        final Object user = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(request);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(request)).andReturn(null);
        cs.store(user, request);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() {
                assertNull(SecurityContext.getCurrentUser());
                EnhancedSecurityContext.login(user);
                called.set(true);
            }
        };

        DefaultCredentialStorageWorkflow csw = new DefaultCredentialStorageWorkflow(request, cs);
        csw.perform(wc);
        assertTrue(called.get());
        EasyMock.verify(request, cs);
    }

    @Test
    public void testLogout() throws IOException, ServletException {
        final Object user = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(request);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(request)).andReturn(user);
        cs.remove(request);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() {
                assertNotNull(EnhancedSecurityContext.getCurrentUser());
                EnhancedSecurityContext.logout();
                called.set(true);
            }
        };

        DefaultCredentialStorageWorkflow csw = new DefaultCredentialStorageWorkflow(request, cs);
        csw.perform(wc);
        assertNull(EnhancedSecurityContext.getCurrentUser());
        assertTrue(called.get());
        EasyMock.verify(request, cs);
    }

    @Test
    public void testUpdate() throws IOException, ServletException {
        final Object user = new Object();
        final Object newUser = new Object();

        EnhancedSecurityContext.setProvider(new JCatapultSecurityContextProvider(null));

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(request);

        CredentialStorage cs = EasyMock.createStrictMock(CredentialStorage.class);
        EasyMock.expect(cs.locate(request)).andReturn(user);
        cs.store(newUser, request);
        EasyMock.replay(cs);

        final AtomicBoolean called = new AtomicBoolean(false);
        WorkflowChain wc = new WorkflowChain() {
            public void continueWorkflow() {
                assertNotNull(EnhancedSecurityContext.getCurrentUser());
                EnhancedSecurityContext.update(newUser);
                called.set(true);
            }
        };

        DefaultCredentialStorageWorkflow csw = new DefaultCredentialStorageWorkflow(request, cs);
        csw.perform(wc);
        assertSame(newUser, EnhancedSecurityContext.getCurrentUser());
        assertTrue(called.get());
        EasyMock.verify(request, cs);
    }
}