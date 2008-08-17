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
package org.jcatapult.security.servlet.auth;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.security.auth.AuthorizationException;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface handles authorization exceptions that are thrown by the
 * {@link org.jcatapult.security.auth.Authorizer} out to the {@link DefaultAuthorizationWorkflow}.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultAuthorizationExceptionHandler.class)
public interface AuthorizationExceptionHandler {
    /**
     * Handles the exception.
     *
     * @param   exception The exception that was thrown.
     * @param   chain The workflow chain in case the implementation wants to keep going down
     *          the chain.
     * @throws  ServletException If something goes wrong during the exception handling.
     * @throws  IOException If something goes wrong during the exception handling.
     */
    void handle(AuthorizationException exception,
        WorkflowChain chain)
    throws ServletException, IOException;
}