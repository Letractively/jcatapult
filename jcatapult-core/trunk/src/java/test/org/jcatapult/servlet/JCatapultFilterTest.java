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
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.easymock.EasyMock;
import org.jcatapult.database.DatabaseTools;
import org.junit.Test;

import com.google.inject.Injector;
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
    public void testGuice() throws ServletException, IOException {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        context.setAttribute(EasyMock.isA(String.class), EasyMock.isA(Injector.class));
        EasyMock.replay(context);

        FilterConfig filterConfig = EasyMock.createStrictMock(FilterConfig.class);
        EasyMock.expect(filterConfig.getServletContext()).andReturn(context);
        EasyMock.replay(filterConfig);

        JCatapultFilter filter = new JCatapultFilter();
        filter.setInitGuice(true);
        filter.setGuiceModules("org.jcatapult.guice.ConfigurationModule");
        filter.setJpaEnabled(false);
        filter.init(filterConfig);

        EasyMock.verify(context);
        EasyMock.verify(filterConfig);
    }

    @Test
    public void testJPA() throws ServletException, IOException {
        MockJNDI jndi = new MockJNDI();
        DatabaseTools.setupJDBCandJNDI(jndi, "jcatapult_core_test");
        jndi.activate();

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(context);

        FilterConfig filterConfig = EasyMock.createStrictMock(FilterConfig.class);
        EasyMock.expect(filterConfig.getServletContext()).andReturn(context);
        EasyMock.replay(filterConfig);

        JCatapultFilter filter = new JCatapultFilter();
        filter.setInitGuice(false);
        filter.setJpaEnabled(true);
        filter.init(filterConfig);

        EasyMock.verify(context);
        EasyMock.verify(filterConfig);
    }
}