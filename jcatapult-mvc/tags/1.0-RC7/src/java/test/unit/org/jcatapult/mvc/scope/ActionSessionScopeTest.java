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
package org.jcatapult.mvc.scope;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the action session scope.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ActionSessionScopeTest {
    @Test
    public void testGet() {
        Object value = new Object();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Map<String, Object> as = new HashMap<String, Object>();
        map.put("org.example.action.user.Edit", as);
        as.put("test", value);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultActionSession")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(new DefaultActionInvocation(new Edit(), null, null, null));
        EasyMock.replay(ais);

        ActionSessionScope scope = new ActionSessionScope(request, ais);
        assertSame(value, scope.get("test"));

        EasyMock.verify(session, request, ais);
    }

    @Test
    public void testFailedGetNoAction() {
        Object value = new Object();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Map<String, Object> as = new HashMap<String, Object>();
        map.put("org.example.action.user.Edit", as);
        as.put("test", value);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultActionSession")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(new DefaultActionInvocation(null, null, null, null));
        EasyMock.replay(ais);

        ActionSessionScope scope = new ActionSessionScope(request, ais);
        try {
            scope.get("test");
            fail("Should have failed");
        } catch (IllegalStateException e) {
            // Expected
        }

        EasyMock.verify(session, request, ais);
    }

    @Test
    public void testSet() {
        Object value = new Object();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultActionSession")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(new DefaultActionInvocation(new Edit(), null, null, null));
        EasyMock.replay(ais);

        ActionSessionScope scope = new ActionSessionScope(request, ais);
        scope.set("test", value);
        assertSame(value, map.get("org.example.action.user.Edit").get("test"));

        EasyMock.verify(session, request, ais);
    }

    @Test
    public void testFailedSetNoAction() {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultActionSession")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(new DefaultActionInvocation(null, null, null, null));
        EasyMock.replay(ais);

        ActionSessionScope scope = new ActionSessionScope(request, ais);
        try {
            scope.set("test", new Object());
            fail("Should have failed");
        } catch (IllegalStateException e) {
            // Expected
        }

        EasyMock.verify(session, request, ais);
    }
}