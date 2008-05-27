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
 *
 */
package org.jcatapult.mvc.action;

import javax.servlet.http.HttpServletRequest;

import org.jcatapult.servlet.Workflow;

/**
 * <p>
 * This class defines the mechanism used to locate actions to invoke and
 * invoke them. It also allows other classes to gain access to the action
 * invocation information whenever they need it. This should make every
 * effort to cache this information rather than parse it from the HTTP
 * request each time.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface ActionWorkflow extends Workflow {
    /**
     * Grabs the action invocation information from the HTTP request. This might have already been
     * fetched as part of the workflow process and reside inside the HTTP request as an attribute,
     * or this method might parse determine the action invocation based on the HTTP request.
     *
     * @param   request The request.
     * @return  The ActionInvocation.
     * @throws  MissingActionInvocationException If the action invocation could not be located.
     */
    ActionInvocation fetch(HttpServletRequest request) throws MissingActionInvocationException;
}