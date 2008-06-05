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
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.junit.Test;

/**
 * <p>
 * This class tests the redirect result.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class RedirectResultTest {
    @Test
    public void testFullyQualified() throws IOException, ServletException {
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.setStatus(301);
        response.sendRedirect("http://www.google.com");
        EasyMock.replay(response);

        Redirect redirect = new RedirectImpl("success", "http://www.google.com", true);
        RedirectResult forwardResult = new RedirectResult();
        forwardResult.execute(redirect, null, null, response);

        EasyMock.verify(response);
    }

    @Test
    public void testRelative() throws IOException, ServletException {
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.setStatus(302);
        response.sendRedirect("/foo/bar.jsp");
        EasyMock.replay(response);

        Redirect redirect = new RedirectImpl("success", "/foo/bar.jsp", false);
        RedirectResult forwardResult = new RedirectResult();
        forwardResult.execute(redirect, null, null, response);

        EasyMock.verify(response);
    }

    public class RedirectImpl implements Redirect {
        private final String code;
        private final String uri;
        private final boolean perm;

        public RedirectImpl(String code, String uri, boolean perm) {
            this.code = code;
            this.uri = uri;
            this.perm = perm;
        }

        public String code() {
            return code;
        }

        public String uri() {
            return uri;
        }

        public boolean perm() {
            return perm;
        }

        public Class<? extends Annotation> annotationType() {
            return Redirect.class;
        }
    }
}