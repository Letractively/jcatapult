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

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * <p>
 * This is the default workflow resolver and it
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultWorkflowResolver implements WorkflowResolver {
    private final String[] types;
    private final Injector injector;

    @Inject
    public DefaultWorkflowResolver(@Named("jcatapult.workflows") String workflows, Injector injector) {
        this.types = workflows.split(",");
        this.injector = injector;
    }

    /**
     * {@inheritDoc}
     */
    public List<Workflow> resolve() {
        List<Workflow> result = new ArrayList<Workflow>();
        for (String type : types) {
            try {
                Class<?> klass = Class.forName(type);
                Workflow workflow = (Workflow) injector.getInstance(klass);
                if (workflow != null) {
                    result.add(workflow);
                }
            } catch (ClassNotFoundException e) {
                // Skip it. It isn't in the classpath
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends Workflow>> getTypes() {
        List<Class<? extends Workflow>> results = new ArrayList<Class<? extends Workflow>>();
        for (String type : types) {
            try {
                Class<? extends Workflow> klass = (Class<? extends Workflow>) Class.forName(type);
                results.add(klass);
            } catch (ClassNotFoundException e) {
                // Skip it. It isn't in the classpath
            }
        }

        return results;
    }
}