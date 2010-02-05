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
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.java.naming.MockJNDI;
import static org.easymock.EasyMock.*;
import org.jcatapult.environment.Environment;
import org.jcatapult.persistence.MySQLTools;
import org.jcatapult.persistence.service.jpa.EntityManagerContext;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class is the test case for the JCatapultFilter.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class JCatapultFilterTest {
    @Test
    public void testJPA() throws ServletException, IOException {
        MockJNDI jndi = new MockJNDI();
        MySQLTools.setup(jndi, "jcatapult_persistence_test");
        jndi.bind("java:comp/env/environment", new Environment("unittesting"));
        jndi.activate();

        // Setup the configuration for the static resource workflow
        ServletContext context = createStrictMock(ServletContext.class);
        context.setAttribute("environment", "unittesting");
        expect(context.getRealPath("/WEB-INF/config/config-unittesting.xml")).andReturn(null);
        expect(context.getRealPath("/WEB-INF/config/config-default.xml")).andReturn(null);
        replay(context);

        FilterConfig filterConfig = createStrictMock(FilterConfig.class);
        expect(filterConfig.getServletContext()).andReturn(context);
        replay(filterConfig);

        // Initialize JCatapult via the listener first.
        ServletContextEvent event = new ServletContextEvent(context);
        JCatapultServletContextListener listener = new JCatapultServletContextListener();
        listener.contextInitialized(event);

        JCatapultFilter filter = new JCatapultFilter();
        filter.init(filterConfig);

        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        // Filter
        expect(request.getAttribute(JCatapultFilter.ORIGINAL_REQUEST_URI)).andReturn(null);
        expect(request.getRequestURI()).andReturn("/test");
        request.setAttribute(JCatapultFilter.ORIGINAL_REQUEST_URI, "/test");
        // StaticResourceWorkflow
        expect(request.getRequestURI()).andReturn("/test");
        // Error handling
        expect(request.getAttribute("javax.servlet.error.exception")).andReturn(null);
        expect(request.getAttribute("javax.servlet.jsp.jspException")).andReturn(null);
        replay(request);

        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        replay(response);

        final AtomicBoolean called = new AtomicBoolean(false);
        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) {
                assertNotNull(EntityManagerContext.get());
                called.set(true);
            }
        };

        filter.doFilter(request, response, chain);
        assertTrue(called.get());

        verify(context);
        verify(filterConfig);
    }
}