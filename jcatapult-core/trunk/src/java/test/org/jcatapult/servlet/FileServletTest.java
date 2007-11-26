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
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.guice.InjectorContext;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * <p>
 * This class tests the file servlet.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FileServletTest {
    @Test
    public void testNoInjector() {
        InjectorContext.setInjector(null);
        FileServlet fs = new FileServlet();
        try {
            fs.init();
            Assert.fail("Should have failed because of no injector");
        } catch (ServletException e) {
        }

        Injector injector = Guice.createInjector(new Module(){
            public void configure(Binder binder) {
            }
        });
        InjectorContext.setInjector(injector);
        try {
            fs.init();
            Assert.fail("Should have failed because of no Configuration");
        } catch (ServletException e) {
        }
    }

    @Test
    public void testMissingFile() throws ServletException, IOException {
        final Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("file-servlet.dir")).andReturn("src/java/test/org/jcatapult/servlet");
        EasyMock.replay(configuration);

        Injector injector = Guice.createInjector(new Module(){
            public void configure(Binder binder) {
                binder.bind(Configuration.class).toInstance(configuration);
            }
        });
        InjectorContext.setInjector(injector);

        FileServlet fs = new FileServlet();
        fs.init();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getServletPath()).andReturn("/files");
        EasyMock.expect(request.getRequestURI()).andReturn("/missing-file.xml");
        EasyMock.replay(request);

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.setStatus(404);
        EasyMock.replay(response);

        fs.doGet(request, response);
        EasyMock.verify(request, response);
    }

    @Test
    public void testSuccess() throws ServletException, IOException {
        final Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("file-servlet.dir")).andReturn("src/java/test/org/jcatapult/servlet");
        EasyMock.replay(configuration);

        Injector injector = Guice.createInjector(new Module(){
            public void configure(Binder binder) {
                binder.bind(Configuration.class).toInstance(configuration);
            }
        });
        InjectorContext.setInjector(injector);

        FileServlet fs = new FileServlet();
        fs.init();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getServletPath()).andReturn("/files");
        EasyMock.expect(request.getRequestURI()).andReturn("/test-file.xml");
        EasyMock.replay(request);

        final StringBuilder build = new StringBuilder();
        ServletOutputStream sos = new ServletOutputStream() {
            public void write(int b) throws IOException {
                build.append(new String(new int[]{b}, 0, 1));
            }
        };

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.expect(response.getOutputStream()).andReturn(sos);
        EasyMock.replay(response);

        fs.doGet(request, response);

        Assert.assertEquals("hello", build.toString());
        EasyMock.verify(request, response);
    }
}