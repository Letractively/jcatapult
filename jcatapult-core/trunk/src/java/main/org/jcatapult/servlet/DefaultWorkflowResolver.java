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
package org.jcatapult.servlet;

import java.util.Arrays;
import java.util.List;

import org.jcatapult.jpa.JPAWorkflow;

import com.google.inject.Inject;

/**
 * <p>
 * This is the default workflow resolver and it currently only returns an
 * instance of the {@link org.jcatapult.jpa.JPAWorkflow}
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultWorkflowResolver implements WorkflowResolver {
    private final JPAWorkflow jpaWorkflow;
    private final StaticResourceWorkflow staticResourceWorkflow;

    @Inject
    public DefaultWorkflowResolver(JPAWorkflow jpaWorkflow, StaticResourceWorkflow staticResourceWorkflow) {
        this.jpaWorkflow = jpaWorkflow;
        this.staticResourceWorkflow = staticResourceWorkflow;
    }

    public List<Workflow> resolve() {
        return Arrays.asList(staticResourceWorkflow, jpaWorkflow);
    }
}