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
package org.jcatapult.mvc.message.scope;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This interface defines the handler for a specific message scope.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Scope {
    /**
     * Retrieve a Map of all of the field messages stored in the scope. This Map is not live and
     * modifications are not stored in the scope.
     *
     * @param   request The request.
     * @param   type The type of messages to retrieve.
     * @param   action The current action.
     * @return  The Map and never null.
     */
    Map<String, List<String>> getFieldMessages(HttpServletRequest request, MessageType type, Object action);

    /**
     * Sets a field message.
     *
     * @param   request The request.
     * @param   type The type of messages to retrieve.
     * @param   action The current action.
     * @param   fieldName The name of the field.
     * @param   message The message.
     */
    void setFieldMessages(HttpServletRequest request, MessageType type, Object action, String fieldName, String message);

    /**
     * Retrieve a List of all of the action messages stored in the scope. This List is not live and
     * modifications are not stored in the scope.
     *
     * @param   request The request.
     * @param   type The type of messages to retrieve.
     * @param   action The current action.
     * @return  The List and never null.
     */
    List<String> getActionMessages(HttpServletRequest request, MessageType type, Object action);

    /**
     * Sets an action message.
     *
     * @param   request The request.
     * @param   type The type of messages to retrieve.
     * @param   action The current action.
     * @param   message The message.
     */
    void setActionMessages(HttpServletRequest request, MessageType type, Object action, String message);

    /**
     * @return  The MessageScope that this scope is associated with.
     */
    MessageScope scope();
}