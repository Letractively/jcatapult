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
package org.jcatapult.mvc;

import java.io.IOException;
import static java.util.Arrays.*;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.ActionInvocationWorkflow;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.parameters.ParameterWorkflow;
import org.jcatapult.mvc.scope.ScopeWorkflow;
import org.jcatapult.servlet.SubWorkflowChain;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the main entry point for the JCatapult MVC. It creates the
 * default workflow that is used to process requests.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MVCWorkflow implements Workflow {
    private List<Workflow> workflows;

    @Inject
    public MVCWorkflow(LocaleWorkflow localeWorkflow, ActionMappingWorkflow actionMappingWorkflow,
            ScopeWorkflow scopeWorkflow, ParameterWorkflow parameterWorkflow, /* ValidationWorkflow validationWorkflow,*/
            ActionInvocationWorkflow actionInvocationWorkflow) {
        workflows = asList(localeWorkflow, actionMappingWorkflow, scopeWorkflow, parameterWorkflow, actionInvocationWorkflow);
    }

    /**
     * Creates a sub-chain of the MVC workflows and invokes it.
     *
     * @param   request The request.
     * @param   response The response.
     * @param   chain The chain.
     * @throws  IOException If the sub-chain throws an IOException
     * @throws  ServletException If the sub-chain throws an ServletException
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        SubWorkflowChain subChain = new SubWorkflowChain(workflows, chain);
        subChain.doWorkflow(request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }
}