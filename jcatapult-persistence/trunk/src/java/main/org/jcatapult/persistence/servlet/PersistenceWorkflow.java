/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.servlet;

import javax.servlet.ServletException;
import java.io.IOException;

import com.google.inject.Inject;
import org.jcatapult.persistence.service.jdbc.JDBCService;
import org.jcatapult.persistence.service.jpa.JPAService;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

/**
 * <p>
 * This class is the JCatapult persistence workflow that cleans up all the persistence resources
 * after the request has been completed. This closes the JDBC connection, releases the JPA entity
 * manager, and removes the transaction context (if one exists).
 * </p>
 *
 * @author Brian Pontarelli
 */
public class PersistenceWorkflow implements Workflow {
    private final JDBCService jdbcService;
    private final JPAService jpaService;

    @Inject
    public PersistenceWorkflow(JDBCService jdbcService, JPAService jpaService) {
        this.jdbcService = jdbcService;
        this.jpaService = jpaService;
    }

    /**
     * Tears down all of the resources and the transaction context if any of them exist.
     *
     * @param   workflowChain The chain.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(WorkflowChain workflowChain) throws IOException, ServletException {
        try {
            // Proceed down the chain
            workflowChain.continueWorkflow();
        } finally {
            jdbcService.tearDownConnection();
            jpaService.tearDownEntityManager();
        }
    }
}
