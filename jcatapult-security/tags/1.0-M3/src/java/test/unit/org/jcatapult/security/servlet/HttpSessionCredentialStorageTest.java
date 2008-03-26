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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This tests the http session credential storage.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class HttpSessionCredentialStorageTest {
    @Test
    public void testLocateWithSession() {
        Object user = new Object();
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapult.security.servlet.http.session.credentials")).andReturn(user);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(false)).andReturn(session);
        EasyMock.replay(request);

        HttpSessionCredentialStorage hscs = new HttpSessionCredentialStorage();
        Object found = hscs.locate(request);
        assertNotNull(found);
        assertSame(user, found);
        EasyMock.verify(session, request);
    }

    @Test
    public void testLocateWithoutSession() {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(false)).andReturn(null);
        EasyMock.replay(request);

        HttpSessionCredentialStorage hscs = new HttpSessionCredentialStorage();
        Object found = hscs.locate(request);
        assertNull(found);
        EasyMock.verify(request); 
    }

    @Test
    public void testStore() {
        Object user = new Object();
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        session.setAttribute("jcatapult.security.servlet.http.session.credentials", user);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(true)).andReturn(session);
        EasyMock.replay(request);

        HttpSessionCredentialStorage hscs = new HttpSessionCredentialStorage();
        hscs.store(user, request);
        EasyMock.verify(session, request);
    }

    @Test
    public void testRemoveWithSession() {
        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        session.removeAttribute("jcatapult.security.servlet.http.session.credentials");
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(false)).andReturn(session);
        EasyMock.replay(request);

        HttpSessionCredentialStorage hscs = new HttpSessionCredentialStorage();
        hscs.remove(request);
        EasyMock.verify(session, request);
    }

    @Test
    public void testRemoveWithoutSession() {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(false)).andReturn(null);
        EasyMock.replay(request);

        HttpSessionCredentialStorage hscs = new HttpSessionCredentialStorage();
        hscs.remove(request);
        EasyMock.verify(request);
    }
}