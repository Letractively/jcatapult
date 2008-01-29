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
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jcatapult.security.servlet.auth.AuthorizationWorkflow;
import org.jcatapult.security.servlet.login.LoginWorkflow;
import org.jcatapult.security.servlet.saved.SavedRequestWorkflow;
import org.jcatapult.servlet.SubWorkflowChain;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the main security workflow that attaches to the JCatapult
 * Workflow system and provides the functionality for handling all the JCatapult
 * security needs. In fact, this is a sub-workflow. It uses a number of other
 * Workflow implementations in a specific order. Here is the default ordering
 * of the Workflows this class uses.
 * </p>
 *
 * <p>
 * This class uses {@link SubWorkflowChain} to handle the call to all of
 * the workflows in the constructor. These are called in this order:
 * </p>
 *
 * <ol>
 * <li>{@link CredentialStorageWorkflow}</li>
 * <li>{@link LoginWorkflow}</li>
 * <li>{@link AuthorizationWorkflow}</li>
 * </ol>
 *
 * @author  Brian Pontarelli
 */
public class SecurityWorkflow implements Workflow {
    private final CredentialStorageWorkflow credentialStorageWorkflow;
    private final SavedRequestWorkflow savedRequestWorkflow;
    //    private final RememberMeWorkflow rememberMeWorkflow;
    private final LoginWorkflow loginWorkflow;
    private final AuthorizationWorkflow authorizationWorkflow;

    @Inject
    public SecurityWorkflow(CredentialStorageWorkflow credentialStorageWorkflow, SavedRequestWorkflow savedRequestWorkflow,
            LoginWorkflow loginWorkflow, AuthorizationWorkflow authorizationWorkflow) {
        this.credentialStorageWorkflow = credentialStorageWorkflow;
        this.savedRequestWorkflow = savedRequestWorkflow;
        this.loginWorkflow = loginWorkflow;
        this.authorizationWorkflow = authorizationWorkflow;
    }

    /**
     * Creates a sub-workflow chain that calls the sub-workflows in the order from the class comment.
     *
     * @param   request The request which is passed to the sub-workflow chain.
     * @param   response The response which is passed to the sub-workflow chain.
     * @param   workflowChain The workflow chain, which is the end point of the sub-workflow chain.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        SubWorkflowChain chain = new SubWorkflowChain(Arrays.asList(credentialStorageWorkflow, savedRequestWorkflow,
            loginWorkflow, authorizationWorkflow), workflowChain);
        chain.doWorkflow(request, response);
    }

    public void destroy() {
    }
}