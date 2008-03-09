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
package org.jcatapult.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

import org.apache.struts2.dispatcher.Dispatcher;
import org.easymock.EasyMock;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.AbstractModule;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * <p>
 * This class can be used for testing classes that depend on
 * injection via the {@link org.jcatapult.guice.WebModule}.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class WebBaseTest extends JPABaseTest {
    protected ServletContext servletContext;
    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected boolean setupStruts = true;

    /**
     * Default constructor.
     */
    protected WebBaseTest() {
    }

    /**
     * Alternate constructor.
     *
     * @param   setupStruts Determines if Struts is setup for testing or not. Defaults to true.
     */
    protected WebBaseTest(boolean setupStruts) {
        this.setupStruts = setupStruts;
    }

    /**
     * Constructs the EntityManager and puts it in the context.
     */
    @Before
    @Override
    public void setUp() {
        setUpWeb();
        super.setUp();
        setUpStruts();
    }

    /**
     * Returns the ServletContext that is an EasyMock mock object that can be programmed and replayed.
     * This hasn't been touched at all.
     *
     * @return  The ServletContext or null if this project isn't a web project.
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Sets up the web layout, if the project has it. This will allow things like email templates and
     * configuration files to be loaded from the correct location. This also sets up the ServletContext
     * mock that can be used by tests and therefore removes the usage of the ThreadLocal that is a
     * hack for Guice.
     */
    public void setUpWeb() {
        final String webRootDir = getWebRootDir();
        if (new File(webRootDir).exists()) {
            logger.info("Project is a web project or a component.  Setting up web test support.");
            modules.add(new AbstractModule() {
                protected void configure() {
                    bind(ContainerResolver.class).toInstance(new ContainerResolver() {
                        public String getRealPath(String path) {
                            String realPath = webRootDir + "/" + path;
                            File f = new File(realPath);
                            if (f.exists()) {
                                return realPath;
                            }

                            return null;
                        }

                        public URL getResource(String path) {
                            String resource = webRootDir + "/" + path;
                            try {
                                File f = new File(resource);
                                if (f.exists()) {
                                    return f.toURI().toURL();
                                }

                                return null;
                            } catch (MalformedURLException e) {
                                return null;
                            }
                        }
                    });
                }
            });
        }

        // Setup servlet context
        this.servletContext = EasyMock.createStrictMock(ServletContext.class);
        ServletObjectsHolder.setServletContext(this.servletContext);
    }

    /**
     * Sets up the Struts2 objects that might be required by some test cases such as the ValueStack
     * and ActionContext.
     */
    protected void setUpStruts() {
        if (setupStruts) {
            ServletContext servletContext = EasyMock.createNiceMock(ServletContext.class);
            EasyMock.replay(servletContext);

            Map<String, String> params = new HashMap<String, String>();
            Dispatcher du = new Dispatcher(servletContext, params);
            du.init();
            Dispatcher.setInstance(du);

            // Reset the value stack
            ValueStack stack = du.getContainer().getInstance(ValueStackFactory.class).createValueStack();
            stack.getContext().put(ActionContext.CONTAINER, du.getContainer());
            ActionContext.setContext(new ActionContext(stack.getContext()));

            configurationManager = du.getConfigurationManager();
            configuration = configurationManager.getConfiguration();
            container = configuration.getContainer();
        }
    }

    /**
     * Returns the directory of the web root
     *
     * @return web root directory
     */
    protected String getWebRootDir() {
        return "web";
    }
}
