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
package org.jcatapult.servlet;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.jcatapult.jpa.JPAWorkflow;

import com.google.inject.Inject;

/**
 * <p>
 * This is the core workflow that includes the JPA and static resource workflows.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class CoreWorkflow implements Workflow {
    private final JPAWorkflow jpaWorkflow;
    private final StaticResourceWorkflow staticResourceWorkflow;

    @Inject
    public CoreWorkflow(JPAWorkflow jpaWorkflow, StaticResourceWorkflow staticResourceWorkflow) {
        this.jpaWorkflow = jpaWorkflow;
        this.staticResourceWorkflow = staticResourceWorkflow;
    }

    /**
     * Creates a sub-chain and invokes it.
     *
     * @param   request Passed to the sub chain.
     * @param   response Passed to the sub chain.
     * @param   chain The end of the sub chain.
     * @throws  IOException If the sub chain throws.
     * @throws  ServletException If the sub chain throws.
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        SubWorkflowChain sub = new SubWorkflowChain(Arrays.asList(staticResourceWorkflow, jpaWorkflow), chain);
        sub.doWorkflow(request, response);
    }

    /**
     * Empty.
     */
    public void destroy() {
    }
}