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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.easymock.EasyMock;
import org.jcatapult.persistence.test.JPABaseTest;
import org.junit.Before;
import org.junit.Ignore;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * <p>
 * This class can be used for testing classes that depend on
 * injection via the {@link org.jcatapult.servlet.guice.WebModule}.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class WebBaseTest extends JPABaseTest {
    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected boolean setupStruts = false;
    protected boolean get = true;

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
        super.setUp();
        setUpStruts();
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
            ActionContext ac = new ActionContext(stack.getContext());
            ac.setName(getActionName());
            ActionContext.setContext(ac);

            configurationManager = du.getConfigurationManager();
            configuration = configurationManager.getConfiguration();
            container = configuration.getContainer();

            ServletActionContext.setServletContext(servletContext);
            ServletActionContext.setRequest(request);
        }
    }

    /**
     * Returns the action name. This defaults to the string 'null' but
     * should be overridden by concrete base tests to return the name of the action
     * being tested.  This method was not made abstract because I didn't want to assume that
     * all unit tests sub-classing WebBaseTest would necessarily be testing an Action.
     *
     * @return the name of the action
     */
    protected String getActionName() {
        return "null";
    }
}