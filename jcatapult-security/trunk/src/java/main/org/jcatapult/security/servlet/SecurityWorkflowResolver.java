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
package org.jcatapult.security.servlet;

import java.util.List;
import java.util.ArrayList;

import org.jcatapult.servlet.DefaultWorkflowResolver;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.jpa.JPAWorkflow;

import com.google.inject.Inject;

/**
 * <p>
 * This class extends the {@link DefaultWorkflowResolver} in order to add the
 * {@link SecurityWorkflow} to the front of the chain.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class SecurityWorkflowResolver extends DefaultWorkflowResolver {
    private final SecurityWorkflow securityWorkflow;

    @Inject
    public SecurityWorkflowResolver(JPAWorkflow jpaWorkflow, SecurityWorkflow securityWorkflow) {
        super(jpaWorkflow);
        this.securityWorkflow = securityWorkflow;
    }

    @Override
    public List<Workflow> resolve() {
        List<Workflow> workflows = new ArrayList<Workflow>(super.resolve());
        workflows.add(securityWorkflow);
        return workflows;
    }
}