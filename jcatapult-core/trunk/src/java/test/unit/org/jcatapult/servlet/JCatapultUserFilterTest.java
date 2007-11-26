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
package org.jcatapult.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easymock.EasyMock;
import org.jcatapult.acegi.ACEGISecurityContextProvider;
import org.jcatapult.acegi.TestUserAdapter;
import org.jcatapult.acegi.TestUser;
import org.jcatapult.security.SecurityContext;
import org.junit.Test;

/**
 * <p>
 * This class is the test case for the JCatapultUserFilter.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class JCatapultUserFilterTest {
    @Test
    public void testNonProxy() throws ServletException, IOException {
        FilterConfig filterConfig = EasyMock.createStrictMock(FilterConfig.class);
        EasyMock.replay(filterConfig);

        JCatapultUserFilter filter = new JCatapultUserFilter();
        filter.init(filterConfig);

        TestUser user = new TestUser();
        SecurityContext.setProvider(new ACEGISecurityContextProvider(new TestUserAdapter()));
        SecurityContext.login(user);

        ServletRequest request = EasyMock.createStrictMock(ServletRequest.class);
        request.setAttribute("user", user);
        EasyMock.replay(request);

        ServletResponse response = EasyMock.createStrictMock(ServletResponse.class);
        EasyMock.replay(response);

        FilterChain chain = EasyMock.createStrictMock(FilterChain.class);
        chain.doFilter(request, response);
        EasyMock.replay(chain);

        filter.doFilter(request, response, chain);

        EasyMock.verify(filterConfig);
        EasyMock.verify(request);
        EasyMock.verify(response);
        EasyMock.verify(chain);
    }

    @Test
    public void testProxy() throws ServletException, IOException {
        FilterConfig filterConfig = EasyMock.createStrictMock(FilterConfig.class);
        EasyMock.replay(filterConfig);

        JCatapultUserFilter filter = new JCatapultUserFilter();
        filter.setProxyUser(true);
        filter.setUserClass("org.jcatapult.acegi.TestUser");
        filter.init(filterConfig);

        TestUser user = new TestUser();
        SecurityContext.setProvider(new ACEGISecurityContextProvider(new TestUserAdapter()));
        SecurityContext.login(user);

        ServletRequest request = EasyMock.createStrictMock(ServletRequest.class);
        request.setAttribute(EasyMock.isA(String.class), EasyMock.isA(TestUser.class));
        EasyMock.replay(request);

        ServletResponse response = EasyMock.createStrictMock(ServletResponse.class);
        EasyMock.replay(response);

        FilterChain chain = EasyMock.createStrictMock(FilterChain.class);
        chain.doFilter(request, response);
        EasyMock.replay(chain);

        filter.doFilter(request, response, chain);

        EasyMock.verify(filterConfig);
        EasyMock.verify(request);
        EasyMock.verify(response);
        EasyMock.verify(chain);
    }
}