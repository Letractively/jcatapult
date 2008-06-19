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
package org.jcatapult.security.servlet.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.security.JCatapultSecurityException;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface is used to handle all exceptions that are thrown when
 * a login fails. The only two exceptions that implements should be
 * concerned with are {@link org.jcatapult.security.login.InvalidUsernameException}
 * and {@link org.jcatapult.security.login.InvalidPasswordException}.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultLoginExceptionHandler.class)
public interface LoginExceptionHandler {
    /**
     * Handles the exception.
     *
     * @param   exception The exception that was thrown.
     * @param   chain The workflow chain in case the implementation wants to keep going down
     *          the chain.
     * @throws  ServletException If something goes wrong during the exception handling.
     * @throws  IOException If something goes wrong during the exception handling.
     */
    void handle(JCatapultSecurityException exception,
        WorkflowChain chain)
    throws ServletException, IOException;
}