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
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.result.Result;
import org.jcatapult.mvc.action.result.ResultInvocation;
import org.jcatapult.mvc.action.result.ResultInvocationProvider;
import org.jcatapult.mvc.action.result.ResultRegistry;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This class is the default implementation of the action invocation workflow.
 * It looks up the ActionInvocation using the ActionMappingWorkflow and the
 * invokes the action using reflection.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultActionInvocationWorkflow implements ActionInvocationWorkflow {
    private final ActionMappingWorkflow actionMappingWorkflow;
    private final ResultInvocationProvider resultInvocationProvider;
    private final ResultRegistry resultRegistry;

    @Inject
    public DefaultActionInvocationWorkflow(ActionMappingWorkflow actionMappingWorkflow,
            ResultInvocationProvider resultInvocationProvider, ResultRegistry resultRegistry) {
        this.actionMappingWorkflow = actionMappingWorkflow;
        this.resultInvocationProvider = resultInvocationProvider;
        this.resultRegistry = resultRegistry;
    }

    /**
     * Performs the action invocation using this process.
     *
     * <h3>Action-less request</h3>
     * <ol>
     * <li>Lookup an action-less result invocation</li>
     * <li>If it doesn't exist, continue down the chain</li>
     * <li>If it does exist, call the ResultRegistry to find the Result</li>
     * <li>Invoke the Result</li>
     * </ul>
     *
     * <h3>Action request</h3>
     * <ol>
     * <li>Invoke the action</li>
     * <li>Lookup an result invocation using the action invocation, action URI and result code from the action</li>
     * <li>If it doesn't exist, error out</li>
     * <li>If it does exist, call the ResultRegistry to find the Result</li>
     * <li>Invoke the Result</li>
     * </ul>
     *
     * @param   request The request.
     * @param   response The response.
     * @param   chain The chain.
     * @throws  IOException If the chain throws an IOException.
     * @throws  ServletException If the chain throws a ServletException or if the result can't be found.
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        ActionInvocation invocation = actionMappingWorkflow.fetch(request);
        ResultInvocation resultInvocation;
        if (invocation.action() == null) {
            // Try a default result mapping just for the URI
            String uri = request.getRequestURI();
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }

            resultInvocation = resultInvocationProvider.lookup(uri);
            if (resultInvocation == null) {
                chain.doWorkflow(request, response);
                return;
            }
        } else {
            Object action = invocation.action();
            String resultCode = execute(action);
            resultInvocation = resultInvocationProvider.lookup(invocation, invocation.actionURI(), resultCode);
            if (resultInvocation == null) {
                response.setStatus(404);
                throw new ServletException("Missing result for action class [" +
                    invocation.configuration().actionClass() + "] uri [" + invocation.actionURI() +
                    "] and result code [" + resultCode + "]");
            }
        }

        Annotation annotation = resultInvocation.annotation();
        Result result = resultRegistry.lookup(annotation.annotationType());
        if (result == null) {
            throw new ServletException("Unmapped result annotationType [" + annotation.getClass() +
                "]. You probably need to define a Result implementation that maps to this annotationType " +
                "and then add that Result implementation to your Guice Module.");
        }

        result.execute(annotation, invocation, request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }

    /**
     * Invokes the execute method on the action.
     *
     * @param   action The action.
     * @return  The result code from the execute method and never null.
     * @throws  ServletException If the execute method doesn't exist, has the wrong signature, couldn't
     *          be invoked, threw an exception or returned null.
     */
    protected String execute(Object action) throws ServletException {
        try {
            Method method = action.getClass().getMethod("execute");
            if (method.getReturnType() != String.class) {
                throw new ServletException("The action class [" + action.getClass() +
                    "] has defined an execute method that is invalid. Execute methods must match the " +
                    "signature [public String execute()].");
            }

            String result = (String) method.invoke(action);
            if (result == null) {
                throw new ServletException("The action class [" + action.getClass() + "] returned " +
                    "null for the result code. Execute methods must never return null.");
            }

            return result;
        } catch (NoSuchMethodException e) {
            throw new ServletException("The action class [" + action.getClass() +
                "] is missing a valid execute method with the signature [public String " +
                "execute()].");
        } catch (InvocationTargetException e) {
            throw new ServletException("The action class [" + action.getClass() + "] threw an exception.",
                e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new ServletException("The action class [" + action.getClass() + "] has defined an " +
                "execute method that is not invalid because it cannot be accessed.", e);
        }
    }
}