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
package org.jcatapult.config;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.jcatapult.container.ServletContainerResolver;
import org.jcatapult.environment.JNDIEnvironmentResolver;
import org.jcatapult.servlet.ServletContextHolder;
import org.junit.Test;

import net.java.naming.MockJNDI;

/**
 * <p>
 * This tests the environment aware configuration.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class EnvironmentAwareConfigurationTest {
    @Test
    public void testEnvironmentAwareConfiguration() throws Exception {
        MockJNDI jndi = new MockJNDI();
        jndi.bind("java:comp/env/environment", "development");
        jndi.activate();

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        ServletContextHolder.setServletContext(context);
        EasyMock.expect(context.getRealPath("/WEB-INF/config/config-default.xml")).
            andReturn("src/java/test/unit/org/jcatapult/config/config-default.xml");
        EasyMock.expect(context.getRealPath("/WEB-INF/config/config-development.xml")).
            andReturn("src/java/test/unit/org/jcatapult/config/config-development.xml");
        EasyMock.replay(context);

        EnvironmentAwareConfiguration config = new EnvironmentAwareConfiguration(new JNDIEnvironmentResolver(),
            new ServletContainerResolver(context), "/WEB-INF/config");
        config.getString("dev", "dev-value");
        config.getString("default", "default-value");
        EasyMock.verify(context);
    }
}