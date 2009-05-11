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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.jcatapult.mvc.scope.annotation.Flash;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the flash scope.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FlashScopeTest {
    @Test
    public void testGetRequest() {
        Object value = new Object();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test", value);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultFlash")).andReturn(map);
        EasyMock.replay(request);

        FlashScope scope = new FlashScope(request);
        assertSame(value, scope.get("test", new Flash() {
            public String value() {
                return "##field-name##";
            }

            public Class<? extends Annotation> annotationType() {
                return Flash.class;
            }
        }));

        EasyMock.verify(request);
    }

    @Test
    public void testGetRequestDifferentKey() {
        Object value = new Object();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("other", value);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultFlash")).andReturn(map);
        EasyMock.replay(request);

        FlashScope scope = new FlashScope(request);
        assertSame(value, scope.get("test", new Flash() {
            public String value() {
                return "other";
            }

            public Class<? extends Annotation> annotationType() {
                return Flash.class;
            }
        }));

        EasyMock.verify(request);
    }

    @Test
    public void testGetFromSession() {
        Object value = new Object();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test", value);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultFlash")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultFlash")).andReturn(new HashMap<String, Object>());
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        FlashScope scope = new FlashScope(request);
        assertSame(value, scope.get("test", new Flash() {
            public String value() {
                return "##field-name##";
            }

            public Class<? extends Annotation> annotationType() {
                return Flash.class;
            }
        }));

        EasyMock.verify(request, session);
    }

    @Test
    public void testGetFromSessionDifferentKey() {
        Object value = new Object();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("other", value);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultFlash")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultFlash")).andReturn(new HashMap<String, Object>());
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        FlashScope scope = new FlashScope(request);
        assertSame(value, scope.get("test", new Flash() {
            public String value() {
                return "other";
            }

            public Class<? extends Annotation> annotationType() {
                return Flash.class;
            }
        }));

        EasyMock.verify(request, session);
    }

    @Test
    public void testSet() {
        Object value = new Object();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultFlash")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        FlashScope scope = new FlashScope(request);
        scope.set("test", value, new Flash() {
            public String value() {
                return "##field-name##";
            }

            public Class<? extends Annotation> annotationType() {
                return Flash.class;
            }
        });
        assertSame(value, map.get("test"));

        EasyMock.verify(request, session);
    }

    @Test
    public void testSetDifferentkey() {
        Object value = new Object();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        EasyMock.expect(session.getAttribute("jcatapultFlash")).andReturn(map);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        FlashScope scope = new FlashScope(request);
        scope.set("test", value, new Flash() {
            public String value() {
                return "other";
            }

            public Class<? extends Annotation> annotationType() {
                return Flash.class;
            }
        });
        assertSame(value, map.get("other"));

        EasyMock.verify(request, session);
    }
}