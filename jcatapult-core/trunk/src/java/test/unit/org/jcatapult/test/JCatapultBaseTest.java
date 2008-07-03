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

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.test.servlet.MockHttpServletRequest;
import org.jcatapult.test.servlet.MockHttpServletResponse;
import org.jcatapult.test.servlet.MockServletContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.google.inject.Injector;
import com.google.inject.Module;
import net.java.naming.MockJNDI;
import net.java.util.CollectionTools;

/**
 * <p>
 * This class is a base test for the JCatapult framework that helps to boot-strap
 * testing by setting up JNDI, Guice and other things. This assumes that applications
 * are constructed in the manner specified by the JCatapult documentation in order
 * to find the correct configuration files.
 * </p>
 *
 * @author  Brian Pontarelli and James Humphrey
 */
@Ignore
public abstract class JCatapultBaseTest {
    private static final Logger logger = Logger.getLogger(JCatapultBaseTest.class.getName());
    public static final MockJNDI jndi = new MockJNDI();
    protected List<Module> modules = CollectionTools.list();
    protected Injector injector;
    protected ServletContext servletContext;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ServletContext context;

    /**
     * Sets up a mock JNDI tree and sets the environment to test.
     */
    @BeforeClass
    public static void setUpJNDI() {
        jndi.bind("java:comp/env/environment", "development");
        jndi.activate();
    }

    /**
     * Allows sub-classes to setup a different set of modules to use. This should be called from the
     * constructor.
     *
     * @param   modules The modules to use for injection.
     */
    public void setModules(Module... modules) {
        this.modules = CollectionTools.llist(modules);
    }

    /**
     * Allows sub-classes to setup a different set of modules to use. This should be called from the
     * constructor.
     *
     * @param   modules The modules to use for injection.
     */
    public void addModules(Module... modules) {
        this.modules.addAll(CollectionTools.list(modules));
    }

    /**
     * Sets up Guice and Configuration.
     */
    @Before
    public void setUp() {
        setUpGuice();
        setUpServletObjects();
    }

    /**
     * Creates the servlet request, servlet response and context and puts them into the holder.
     */
    protected void setUpServletObjects() {
        this.context = makeContext();
        this.request = makeRequest(context);
        this.response = makeResponse();

        ServletObjectsHolder.setServletContext(context);
        ServletObjectsHolder.setServletRequest(request);
        ServletObjectsHolder.setServletResponse(response);
    }

    /**
     * Constructs a request whose URI is /test, Locale is US, is a GET and encoded using UTF-8.
     *
     * @param   context The MockServletContext.
     * @return  The mock request.
     */
    protected HttpServletRequest makeRequest(ServletContext context) {
        return new MockHttpServletRequest("/test", Locale.US, false, "UTF-8", (MockServletContext) context);
    }

    /**
     * Constructs a mock response.
     *
     * @return  The mock response.
     */
    protected HttpServletResponse makeResponse() {
        return new MockHttpServletResponse();
    }

    /**
     * Constructs a mock servlet context.
     *
     * @return  The mock context.
     */
    protected ServletContext makeContext() {
        return new MockServletContext();
    }

    /**
     * Sets up the configuration and then the injector.
     */
    public void setUpGuice() {
        StringBuffer moduleNames = new StringBuffer(" ");
        for (Module module : modules) {
            moduleNames.append(module.getClass().getName()).append(" ");
        }

        logger.info("Setting up injection with modules [" + moduleNames.toString() + "]");
        if (modules.size() > 0) {
            GuiceContainer.setGuiceModules(modules.toArray(new Module[modules.size()]));
        }

        GuiceContainer.inject();
        GuiceContainer.initialize();
        injector = GuiceContainer.getInjector();
        injector.injectMembers(this);
    }
}
