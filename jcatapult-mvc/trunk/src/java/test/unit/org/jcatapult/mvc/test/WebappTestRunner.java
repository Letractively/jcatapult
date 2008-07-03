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
package org.jcatapult.mvc.test;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.servlet.MVCWorkflow;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.servlet.WorkflowChain;
import org.jcatapult.test.servlet.MockHttpServletRequest;
import org.jcatapult.test.servlet.MockHttpServletResponse;
import org.jcatapult.test.servlet.MockServletContext;
import org.jcatapult.test.servlet.WebTestHelper;

import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * <p>
 * This class is a test helper that assists in executing MVC actions and
 * testing the entire MVC workflow for the action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class WebappTestRunner {
    public MVCWorkflow workflow;
    public MessageStore messageStore;
    public MockHttpServletRequest request;
    public MockHttpServletResponse response;
    public MockServletContext context;

    @Inject
    public void setServices(MVCWorkflow workflow, MessageStore messageStore) {
        this.workflow = workflow;
        this.messageStore = messageStore;
    }

    public RequestBuilder test(String uri) {
        return new RequestBuilder(uri, this);
    }

    void run(RequestBuilder builder, boolean post) throws IOException, ServletException {
        this.context = makeContext();
        ServletObjectsHolder.setServletContext(context);

        this.request = new MockHttpServletRequest(builder.getParameters(), builder.getUri(), "UTF-8",
            builder.getLocale(), post, context);
        ServletObjectsHolder.setServletRequest(request);

        this.response = makeResponse();
        ServletObjectsHolder.setServletResponse(response);

        GuiceContainer.setGuiceModules(builder.getModules().toArray(new Module[builder.getModules().size()]));
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
        return WebTestHelper.makeContext();
    }
}