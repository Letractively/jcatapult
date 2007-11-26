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

import javax.servlet.ServletContext;

import org.jcatapult.guice.ACEGIWebSecurityModule;
import org.junit.Ignore;

import com.google.inject.Module;
import net.java.util.CollectionTools;

/**
 * <p>
 * This class can be used for testing classes that depend on
 * injection via the {@link org.jcatapult.guice.ACEGIWebSecurityModule}.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class WebSecurityBaseTest extends WebBaseTest {
    protected ServletContext servletContext;

    /**
     * Default constructor to set the {@link org.jcatapult.guice.ACEGIWebSecurityModule} to the list of guice modules by
     * calling the template method {@link #getJCatapultACEGIWebSecurityModule()}.
     */
    protected WebSecurityBaseTest() {
        modules = CollectionTools.<Module>list(getJCatapultACEGIWebSecurityModule());

    }

    /**
     * Returns the {@link org.jcatapult.guice.ACEGIWebSecurityModule} specific to the application being tested. You must
     * implement this method so that the guice injection will work correctly with the JCatapult security framework.
     *
     * @return  {@link org.jcatapult.guice.ACEGIWebSecurityModule} the jcatapult web module that will be added to the list
     *          of modules sent to Guice for injection.
     */
    protected abstract ACEGIWebSecurityModule getJCatapultACEGIWebSecurityModule();
}