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
package org.jcatapult.mvc.action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.action.config.ActionConfiguration;
import org.jcatapult.mvc.action.config.ActionConfigurationProvider;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the default implementation of the ActionWorkflow. During the
 * perform method, this class pulls the action information from the HTTP request
 * URI and loads the action object from the GuiceContainer (for now). The way
 * that the action class is determined is based on the ActionConfigurationProvider
 * interface. This interface is used to create the configuration and cache it.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultActionMappingWorkflow implements ActionMappingWorkflow {
    private final ActionConfigurationProvider actionConfigurationProvider;
    private final ActionInvocationStore actionInvocationStore;
    private final ObjectFactory objectFactory;

    @Inject
    public DefaultActionMappingWorkflow(ActionConfigurationProvider actionConfigurationProvider,
            ActionInvocationStore actionInvocationStore, ObjectFactory objectFactory) {
        this.actionConfigurationProvider = actionConfigurationProvider;
        this.actionInvocationStore = actionInvocationStore;
        this.objectFactory = objectFactory;
    }

    /**
     * Processes the request URI, loads the action configuration, creates the action and stores the
     * invocation in the request.
     *
     * @param   request The request where the URI used to map to an action invocation is pulled from.
     * @param   response The response.
     * @param   chain The workflow chain.
     * @throws  IOException If the chain throws an exception.
     * @throws  ServletException If the chain throws an exception.
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        ActionConfiguration actionConfiguration = actionConfigurationProvider.lookup(uri);
        Object action = null;
        if (actionConfiguration != null) {
            action = objectFactory.create(actionConfiguration.actionClass());
        }

        ActionInvocation invocation = new DefaultActionInvocation(action, uri, actionConfiguration);
        actionInvocationStore.set(invocation);

        chain.doWorkflow(request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }
}