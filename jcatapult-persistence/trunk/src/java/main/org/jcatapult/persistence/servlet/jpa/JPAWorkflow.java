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
package org.jcatapult.persistence.servlet.jpa;

import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;

import org.jcatapult.persistence.service.jpa.JPAService;
import org.jcatapult.servlet.DestroyableWorkflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles setting up the JPA support for each request into the
 * servlet container. This allows JPA classes to be injected into classes
 * using a ThreadLocal storage for EntityManagers. This is also the
 * implementation of the Open-Seesion-In-View pattern.
 * </p>
 *
 * <p>
 * The majority of the work is actually performed by the {@link org.jcatapult.persistence.service.jpa.JPAService}
 * and not this class. This class just delegates to the JPAService and fits
 * into the JCatapult workflow processing.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JPAWorkflow implements DestroyableWorkflow {
    private final JPAService service;

    @Inject
    public JPAWorkflow(JPAService service) {
        this.service = service;
    }

    /**
     * Sets up an entity manager if JPA is enabled.
     *
     * @param   workflowChain The chain.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(WorkflowChain workflowChain) throws IOException, ServletException {
        EntityManagerFactory emf = service.getFactory();
        if (emf != null) {
            try {
                service.setupEntityManager();

                // Proceed down the chain
                workflowChain.continueWorkflow();
            } finally {
                service.tearDownEntityManager();
            }
        } else {
            workflowChain.continueWorkflow();
        }
    }

    /**
     * Closes the EntityManagerFactory and removes the context from the holder.
     */
    public void destroy() {
        service.closeFactory();
    }
}