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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContextEvent;

import org.easymock.EasyMock;
import org.jcatapult.database.DatabaseTools;
import org.jcatapult.jpa.EntityManagerContext;
import static org.junit.Assert.*;
import org.junit.Test;

import net.java.naming.MockJNDI;

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
        DatabaseTools.setupJDBCandJNDI(jndi, "jcatapult_core_test");
        jndi.activate();

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(context);

        FilterConfig filterConfig = EasyMock.createStrictMock(FilterConfig.class);
        EasyMock.replay(filterConfig);

        // Initialize JCatapult via the listener first.
        ServletContextEvent event = new ServletContextEvent(context);
        JCatapultServletContextListener listener = new JCatapultServletContextListener();
        listener.contextInitialized(event);

        JCatapultFilter filter = new JCatapultFilter();
        filter.init(filterConfig);

        ServletRequest request = EasyMock.createStrictMock(ServletRequest.class);
        ServletResponse response  = EasyMock.createStrictMock(ServletResponse.class);
        EasyMock.replay(request, response);

        final AtomicBoolean called = new AtomicBoolean(false);
        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) {
                assertNotNull(EntityManagerContext.get());
                called.set(true);
            }
        };

        filter.doFilter(request, response, chain);
        assertTrue(called.get());

        EasyMock.verify(context);
        EasyMock.verify(filterConfig);
    }
}