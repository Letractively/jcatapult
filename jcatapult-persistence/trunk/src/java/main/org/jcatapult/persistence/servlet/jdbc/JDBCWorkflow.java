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
package org.jcatapult.persistence.servlet.jdbc;

import javax.servlet.ServletException;
import java.io.IOException;

import com.google.inject.Inject;
import org.jcatapult.persistence.service.jdbc.JDBCService;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

/**
 * <p>
 * This class is a JCatapult workflow that provides injections for JDBC connections.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JDBCWorkflow implements Workflow {
    private final JDBCService service;

    @Inject
    public JDBCWorkflow(JDBCService service) {
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
        try {
            // Proceed down the chain
            workflowChain.continueWorkflow();
        } finally {
            service.tearDownConnection();
        }
    }
}
