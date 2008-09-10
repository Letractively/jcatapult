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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
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
    /**
     * HTTP request parameter or scoped attribute from the request that indicates if the result
     * should be executed or not. By default the result is always executed, but this can be used to
     * suppress that behavior.
     */
    public static final String JCATAPULT_EXECUTE_RESULT = "jcatapultExecuteResult";

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ActionConfigurationProvider actionConfigurationProvider;
    private final ActionInvocationStore actionInvocationStore;
    private final ObjectFactory objectFactory;

    @Inject
    public DefaultActionMappingWorkflow(HttpServletRequest request, HttpServletResponse response,
            ActionConfigurationProvider actionConfigurationProvider,
            ActionInvocationStore actionInvocationStore, ObjectFactory objectFactory) {
        this.request = request;
        this.response = response;
        this.actionConfigurationProvider = actionConfigurationProvider;
        this.actionInvocationStore = actionInvocationStore;
        this.objectFactory = objectFactory;
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
        String uri = determineURI();

        // Handle extensions
        String extension = determineExtension(uri);
        if (extension != null) {
            uri = uri.substring(0, uri.length() - extension.length() - 1);
        }

        ActionConfiguration actionConfiguration = actionConfigurationProvider.lookup(uri);
        if (actionConfiguration == null) {
            // Try the index cases. If the URI is /foo/, look for an action config of /foo/index and
            // use it. If the uri is /foo, look for a config of /foo/index and then send a redirect
            // to /foo/
            if (uri.endsWith("/")) {
                actionConfiguration = actionConfigurationProvider.lookup(uri + "index");
            } else {
                actionConfiguration = actionConfigurationProvider.lookup(uri + "/index");
                if (actionConfiguration != null) {
                    response.sendRedirect(uri + "/");
                    return;
                }
            }
        }

        // Okay, no index handling was found and there isn't anything yet, let's search for it, but
        // only if it isn't an index like URI (i.e. not /admin/)
        Deque<String> uriParameters = new ArrayDeque<String>();
        if (actionConfiguration == null && !uri.endsWith("/")) {
            int index = uri.lastIndexOf('/');
            String localURI = uri;
            while (index > 0 && actionConfiguration == null) {
                // Add the restful parameter
                uriParameters.offerFirst(localURI.substring(index + 1));

                // Check if this matches
                localURI = localURI.substring(0, index);
                actionConfiguration = actionConfigurationProvider.lookup(localURI);
                if (actionConfiguration != null && !actionConfiguration.canHandle(uri)) {
                    actionConfiguration = null;
                }

                if (actionConfiguration == null) {
                    index = localURI.lastIndexOf('/');
                }
            }

            if (actionConfiguration == null) {
                uriParameters.clear();
            } else {
                uri = localURI;
            }
        }

        Object action = null;
        if (actionConfiguration != null) {
            action = objectFactory.create(actionConfiguration.actionClass());
        }

        boolean executeResult = executeResult(JCATAPULT_EXECUTE_RESULT);
        ActionInvocation invocation = new DefaultActionInvocation(action, uri, extension,
            uriParameters, actionConfiguration, executeResult, true, null);
        actionInvocationStore.setCurrent(invocation);

        chain.continueWorkflow();

        actionInvocationStore.popCurrent();
    }

    private String determineURI() {
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
        return uri;
    }

    private String determineExtension(String uri) {
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

            if (!good) {
                extension = null;
            }
        }

        return extension;
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