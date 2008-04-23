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
package org.jcatapult.jpa;

import java.io.IOException;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * <p>
 * This class handles setting up JPA in the constructor and then setting up
 * a ThreadLocal EntityManager that can be injected via Guice for each HTTP
 * request.
 * </p>
 *
 * <p>
 * This is a singleton so that the EntityManagerFactory is only created once,
 * when the container is loaded.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class JPAWorkflow implements Workflow {
    private static final Logger logger = Logger.getLogger(JPAWorkflow.class.getName());
    private boolean jpaEnabled;
    private EntityManagerFactory emf;

    /**
     * @param   jpaEnabled If true, JPA will be setup, false it will not. A boolean flag controlled
     *          by the jcatapult configuration property named <strong>jcatapult.jpa.enabled</strong>
     *          that controls whether or not JPA will be initialized and then setup during each request.
     * @param   persistentUnit  The name of the JPA persistent unit to use if JPA is being setup.
     *          This is controlled by the jcatapult configuration property named <strong>jcatapult.jpa.unit</strong>.
     */
    @Inject
    public JPAWorkflow(@Named("jcatapult.jpa.enabled") boolean jpaEnabled,
            @Named("jcatapult.jpa.unit") String persistentUnit) {
        logger.fine("JPA is " + (jpaEnabled ? "enabled" : "disabled"));

        this.jpaEnabled = jpaEnabled;
        if (jpaEnabled) {
            emf = Persistence.createEntityManagerFactory(persistentUnit);
        }
    }

    /**
     * Sets up an entity manager if JPA is enabled.
     *
     * @param   request Passed on.
     * @param   response Passed on.
     * @param   workflowChain The chain.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        if (jpaEnabled) {
            EntityManager entityManager = emf.createEntityManager();
            try {
                // Grab the EntityManager for this request. This is lightweight so don't freak out that
                // this will use resources. It doesn't grab a JDBC connection until it has to.
                EntityManagerContext.set(entityManager);

                // Proceed down the chain
                workflowChain.doWorkflow(request, response);
            } finally {
                // Clear out the context just to be safe.
                EntityManagerContext.remove();
                entityManager.close();
            }
        } else {
            workflowChain.doWorkflow(request, response);
        }
    }

    /**
     * Closes the EntityManagerFactory and removes the context from the holder.
     */
    public void destroy() {
        if (jpaEnabled) {
            emf.close();
        }
    }
}