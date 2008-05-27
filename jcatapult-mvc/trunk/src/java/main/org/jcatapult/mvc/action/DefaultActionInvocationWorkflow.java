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
 *
 */
package org.jcatapult.mvc.action;

import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.result.Result;
import org.jcatapult.mvc.action.result.ResultInvocation;
import org.jcatapult.mvc.action.result.ResultInvocationProvider;
import org.jcatapult.mvc.action.result.ResultRegistry;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the default implementation of the action invocation workflow.
 * It looks up the ActionInvocation using the ActionMappingWorkflow and the
 * invokes the action using reflection.
 * </p>
 *
 * @author  Brian Pontarelli
 */
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
        if (invocation == null) {
            // Try a result mapping
            resultInvocation = resultInvocationProvider.lookup(invocation, request.getRequestURI(), "success");
            if (resultInvocation == null) {
                chain.doWorkflow(request, response);
                return;
            }
        } else {
            Action action = (Action) invocation.action();
            try {
                String resultCode = action.execute();
                resultInvocation = resultInvocationProvider.lookup(invocation, invocation.actionURI(), resultCode);
                if (resultInvocation == null) {
                    throw new RuntimeException("Missing result for action class [" +
                        invocation.configuration().actionClass() + "] uri [" + invocation.actionURI() +
                        "] and result code [" + resultCode + "]");
                }
            } catch (Throwable throwable) {
                // For now, blow chunks
                // TODO handle exceptions some how.
                throw new ServletException(throwable);
            }
        }

        Annotation annotation = resultInvocation.annotation();
        Result result = resultRegistry.lookup(annotation.getClass());
        if (result == null) {
            throw new ServletException("Unknown result annotation [" + annotation.getClass() + "]");
        }

        result.execute(annotation, invocation, request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }
}