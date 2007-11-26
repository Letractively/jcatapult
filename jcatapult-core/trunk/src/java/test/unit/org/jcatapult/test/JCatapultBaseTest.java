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
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.jcatapult.guice.ConfigurationModule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.google.inject.Guice;
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
    protected List<Module> modules = CollectionTools.<Module>list(new ConfigurationModule());
    protected Injector injector;
    protected ServletContext servletContext;

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
        injector = Guice.createInjector(modules);
        injector.injectMembers(this);
    }
}
