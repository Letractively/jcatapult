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
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
    /**
     * HTTP request parameter or scoped attribute from the request that indicates if the result
     * should be executed or not. By default the result is always executed, but this can be used to
     * suppress that behavior.
     */
    public static final String JCATAPULT_EXECUTE_RESULT = "jcatapultExecuteResult";

    private final HttpServletRequest request;
    private final ActionConfigurationProvider actionConfigurationProvider;
    private final ActionInvocationStore actionInvocationStore;
    private final ObjectFactory objectFactory;

    @Inject
    public DefaultActionMappingWorkflow(HttpServletRequest request,
            ActionConfigurationProvider actionConfigurationProvider,
            ActionInvocationStore actionInvocationStore, ObjectFactory objectFactory) {
        this.actionConfigurationProvider = actionConfigurationProvider;
        this.actionInvocationStore = actionInvocationStore;
        this.objectFactory = objectFactory;
        this.request = request;
    }

    /**
     * Processes the request URI, loads the action configuration, creates the action and stores the
     * invocation in the request.
     *
     * @param   chain The workflow chain.
     * @throws  IOException If the chain throws an exception.
     * @throws  ServletException If the chain throws an exception.
     */
    @SuppressWarnings("unchecked")
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        // First, see if they hit a different button
        String uri = null;
        Set<String> keys = request.getParameterMap().keySet();
        for (String key : keys) {
            if (key.startsWith("__jc_a_")) {
                String actionParameterName = key.substring(7);
                String actionParameterValue = request.getParameter(key);
                if (request.getParameter(actionParameterName) != null && actionParameterValue.trim().length() > 0) {
                    uri = actionParameterValue;

                    // Handle relative URIs
                    if (!uri.startsWith("/")) {
                        String requestURI = request.getRequestURI();
                        int index = requestURI.lastIndexOf("/");
                        if (index >= 0) {
                            uri = requestURI.substring(0, index) + "/" + uri;
                        }
                    }
                }
            }
        }

        if (uri == null) {
            uri = request.getRequestURI();
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }
        }

        // Handle extensions
        String extension = null;
        int index = uri.lastIndexOf('.');
        if (index >= 0) {
            extension = uri.substring(index + 1);

            // Sanity check the extension to ensure it is NOT part of a version number like /foo-1.0
            boolean good = false;
            for (int i = 0; i < extension.length(); i++) {
                good = Character.isLetter(extension.charAt(i));
                if (!good) {
                    break;
                }
            }

            if (good) {
                uri = uri.substring(0, index);
            } else {
                extension = null;
            }
        }

        ActionConfiguration actionConfiguration = actionConfigurationProvider.lookup(uri);
        if (actionConfiguration == null) {
            // Try the index cases. If the URI is /foo, try /foo/index
            String indexedURI = (uri.endsWith("/")) ? uri + "index" : uri + "/index";
            actionConfiguration = actionConfigurationProvider.lookup(indexedURI);
        }

        Object action = null;
        if (actionConfiguration != null) {
            action = objectFactory.create(actionConfiguration.actionClass());
        }

        boolean executeResult = executeResult(JCATAPULT_EXECUTE_RESULT);
        ActionInvocation invocation = new DefaultActionInvocation(action, uri, extension,
            actionConfiguration, executeResult, true, null);
        actionInvocationStore.setCurrent(invocation);

        chain.continueWorkflow();

        actionInvocationStore.popCurrent();
    }

    /**
     * Determines if the result should be executed or not.
     *
     * @param   key The key.
     * @return  True of false.
     */
    private boolean executeResult(String key) {
        Object value = request.getParameter(key);
        if (value == null) {
            value = request.getAttribute(key);
        }

        if (value != null && value instanceof String) {
            return value.equals("true");
        }

        return value == null ? true : (Boolean) value;
    }
}