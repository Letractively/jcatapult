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
package org.jcatapult.mvc.action.result;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.junit.Test;

/**
 * <p>
 * This class tests the forward result.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ForwardResultTest {
    @Test
    public void testFullyQualified() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);

        RequestDispatcher dispatcher = EasyMock.createStrictMock(RequestDispatcher.class);
        dispatcher.forward(request, null);
        EasyMock.replay(dispatcher);

        EasyMock.expect(request.getRequestDispatcher("/foo/bar.jsp")).andReturn(dispatcher);
        EasyMock.replay(request);

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(context);

        Forward forward = new ForwardResult.ForwardImpl("/foo/bar.jsp", null);
        ForwardResult forwardResult = new ForwardResult(Locale.CANADA, context, request, null, null, null);
        forwardResult.execute(forward, new DefaultActionInvocation(null, "/foo/bar", null, null));

        EasyMock.verify(context, dispatcher, request);
    }

    @Test
    public void testRelative() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);

        RequestDispatcher dispatcher = EasyMock.createStrictMock(RequestDispatcher.class);
        dispatcher.forward(request, null);
        EasyMock.replay(dispatcher);

        EasyMock.expect(request.getRequestDispatcher("/WEB-INF/content/action/bar.jsp")).andReturn(dispatcher);
        EasyMock.replay(request);

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(context);

        Forward forward = new ForwardResult.ForwardImpl("bar.jsp", null);
        ForwardResult forwardResult = new ForwardResult(Locale.GERMAN, context, request, null, null, null);
        forwardResult.execute(forward, new DefaultActionInvocation(null, "/action", null, null));

        EasyMock.verify(context, dispatcher, request);
    }
}