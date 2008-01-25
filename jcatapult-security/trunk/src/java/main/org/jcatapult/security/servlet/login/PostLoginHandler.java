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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jcatapult.security.servlet.saved.SavedRequestWorkflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the handling of the request and response when
 * a login is successful.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(SavedRequestWorkflow.class)
public interface PostLoginHandler {
    /**
     * Handles a successful login.
     *
     * @param   request The HTTP servlet request.
     * @param   response The HTTP servlet response.
     * @param   workflowChain The workflow chain if the implementation wants to continue down the
     *          workflow chain.
     * @throws  ServletException If the a servlet error occurs.
     * @throws  IOException If an IO error occurs.
     */
    void handle(ServletRequest request, ServletResponse response, WorkflowChain workflowChain)
    throws ServletException, IOException;
}