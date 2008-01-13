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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jcatapult.security.SecurityContext;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class provides a simple mechanism for locating an existing user
 * object using the {@link CredentialStorage} and then storing it into
 * the {@link JCatapultSecurityContextProvider}, which is the default
 * implementation of the {@link org.jcatapult.security.spi.SecurityContextProvider}.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class CredentialStorageWorkflow implements Workflow {
    private final CredentialStorage credentialStorage;

    @Inject
    public CredentialStorageWorkflow(CredentialStorage credentialStorage) {
        this.credentialStorage = credentialStorage;
    }

    /**
     * Looks up the user from the {@link CredentialStorage} and saves it to the context if it exists. In
     * either case, it calls the chain to proceed and removes the object from the context after the chain
     * has returned.
     *
     * @param   request The request.
     * @param   response The response.
     * @param   workflowChain The workflow chain.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws IOException, ServletException {
        Object userObject = credentialStorage.locate(request);
        boolean existing = (userObject != null);
        if (existing) {
            SecurityContext.login(userObject);
        }

        try {
            workflowChain.doWorkflow(request, response);
        } finally {
            // If the user didn't exist before and now it does, store it.
            if (!existing && SecurityContext.getCurrentUser() != null) {
                credentialStorage.store(SecurityContext.getCurrentUser(), request);
            } else if (existing && SecurityContext.getCurrentUser() == null) {
                credentialStorage.remove(request);
            }

            SecurityContext.logout();
        }
    }

    public void destroy() {
    }
}