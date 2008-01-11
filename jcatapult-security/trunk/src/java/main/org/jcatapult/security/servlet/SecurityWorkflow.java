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

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

/**
 * <p>
 * This class is the main security workflow that attaches to the JCatapult
 * Workflow system and provides the functionality for handling all the JCatapult
 * security needs. In fact, this is a sub-workflow. It uses a number of other
 * Workflow implementations in a specific order. Here is the default ordering
 * of the Workflows this class uses.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SecurityWorkflow implements Workflow {
    private final CredentialStorageWorkflow credentialStorageWorkflow;
    private final RememberMeWorkflow rememberMeWorkflow;
    private final LoginWorkflow loginWorkflow;
    private final SavedRequestWorkflow savedRequestWorkflow;
    private final AuthorizationWorkflow authorizationWorkflow;



    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
    }

    public void destroy() {
    }
}