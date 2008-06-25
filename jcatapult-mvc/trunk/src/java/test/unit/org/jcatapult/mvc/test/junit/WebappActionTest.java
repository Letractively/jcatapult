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
package org.jcatapult.mvc.test.junit;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.servlet.MVCWorkflow;
import org.jcatapult.mvc.test.MockHttpServletRequest;
import org.jcatapult.mvc.test.MockHttpServletResponse;
import org.jcatapult.mvc.test.MockHttpSession;
import org.jcatapult.mvc.test.MockServletContext;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.servlet.WorkflowChain;
import org.junit.Ignore;

import com.google.inject.Inject;
import net.java.naming.MockJNDI;

/**
 * <p>
 * This class is a JUnit 4 base test for MVC testing.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class WebappActionTest {
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockServletContext context;

    protected MVCWorkflow workflow;
    protected MessageStore messageStore;

    @Inject
    public void setServices(MVCWorkflow workflow, MessageStore messageStore) {
        this.workflow = workflow;
        this.messageStore = messageStore;
    }

    public RequestBuilder test(String uri) {
        return new RequestBuilder(uri, this);
    }

    void run(RequestBuilder builder, boolean post) throws IOException, ServletException {
        // Set the environment to test
        MockJNDI jndi = new MockJNDI();
        jndi.bind("java:comp/env/environment", "test");
        jndi.activate();

        this.request = new MockHttpServletRequest(builder.getParameters(), builder.getUri(), "UTF-8",
            builder.getLocale(), post);
        ServletObjectsHolder.setServletRequest(request);

        this.response = makeResponse();
        ServletObjectsHolder.setServletResponse(response);

        this.context = makeContext();
        ServletObjectsHolder.setServletContext(context);

        GuiceContainer.inject();
        GuiceContainer.initialize();
        GuiceContainer.getInjector().injectMembers(this);

        workflow.perform(new WorkflowChain() {
            public void continueWorkflow() {
            }
        });
    }

    /**
     * @return  Makes a HttpServletResponse as a nice mock. Sub-classes can override this
     */
    protected MockHttpServletResponse makeResponse() {
        return new MockHttpServletResponse();
    }

    /**
     * @return  Makes a ServletContext as a nice mock. Sub-classes can override this
     */
    protected MockServletContext makeContext() {
        return MockHttpSession.context;
    }
}