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
 *
 */
package org.jcatapult.mvc.action.result;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the default result invocation provider.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultResultInvocationProviderTest {
    @Test
    public void testActionLess() throws MalformedURLException {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(context.getResource("/WEB-INF/content/foo/bar.jsp")).andReturn(null);
        EasyMock.expect(context.getResource("/WEB-INF/content/foo/bar.ftl")).andReturn(null);
        EasyMock.expect(context.getResource("/WEB-INF/content/foo/bar/index.jsp")).andReturn(null);
        EasyMock.expect(context.getResource("/WEB-INF/content/foo/bar/index.ftl")).andReturn(new URL("http://google.com"));
        EasyMock.replay(context);

        DefaultResultInvocationProvider provider = new DefaultResultInvocationProvider(context);
        ResultInvocation invocation = provider.lookup("/foo/bar");
        assertNotNull(invocation);
        assertNull(invocation.resultCode());
        assertEquals("/foo/bar", invocation.uri());
        assertNull(((Forward) invocation.annotation()).code());
        assertEquals("/WEB-INF/content/foo/bar/index.ftl", ((Forward) invocation.annotation()).page());

        EasyMock.verify(context);
    }

    @Test
    public void testActionAnnotation() {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(context);

        TestAction action = new TestAction();
        DefaultResultInvocationProvider provider = new DefaultResultInvocationProvider(context);
        ResultInvocation invocation = provider.lookup(new DefaultActionInvocation(action, null, null), "/foo/bar", "success");
        assertNotNull(invocation);
        assertEquals("success", invocation.resultCode());
        assertEquals("/foo/bar", invocation.uri());
        assertEquals("success", ((Forward) invocation.annotation()).code());
        assertEquals("foo.jsp", ((Forward) invocation.annotation()).page());

        EasyMock.verify(context);
    }
}