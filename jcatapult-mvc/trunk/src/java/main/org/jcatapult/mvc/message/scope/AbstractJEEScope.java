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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This class is an abstract implementation of the Scope interface. It
 * implements all of the Scope methods and forces sub-classes to simply
 * focus on retrieving the Map and List to storage the messages from
 * the correct location.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJEEScope extends AbstractScope {
    /**
     * The location where the field errors are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the field errors Map.
     */
    public static final String FIELD_ERROR_KEY = "jcatapultFieldErrors";

    /**
     * The location where the field messages are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the field messages Map.
     */
    public static final String FIELD_MESSAGE_KEY = "jcatapultFieldMessages";

    /**
     * The location where the action errors are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the action errors List.
     */
    public static final String ACTION_ERROR_KEY = "jcatapultActionErrors";

    /**
     * The location where the action messages are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the action messages List.
     */
    public static final String ACTION_MESSAGE_KEY = "jcatapultActionMessages";

    /**
     * Implements the getFieldScope method in a generic JEE way. This simply uses two template methods
     * that are responsible for looking up the scope in the correct JEE interface.
     *
     * @param   request The request.
     * @param   type The type, which is used to determine the key that the scope is stored under.
     * @param   action The action (not really used).
     * @return  The scope and never null.
     */
    protected Map<String, List<String>> getFieldScope(HttpServletRequest request, MessageType type, Object action) {
        String key = (type == MessageType.ERROR) ? FIELD_ERROR_KEY : FIELD_MESSAGE_KEY;
        Map<String, List<String>> scope = (Map<String, List<String>>) findScope(request, key);
        if (scope == null) {
            scope = new HashMap<String, List<String>>();
            storeScope(request, key, scope);
        }

        return scope;
    }

    /**
     * Implements the getActionScope method in a generic JEE way. This simply uses two template methods
     * that are responsible for looking up the scope in the correct JEE interface.
     *
     * @param   request The request.
     * @param   type The type, which is used to determine the key that the scope is stored under.
     * @param   action The action (not really used).
     * @return  The scope and never null.
     */
    protected List<String> getActionScope(HttpServletRequest request, MessageType type, Object action) {
        String key = (type == MessageType.ERROR) ? ACTION_ERROR_KEY : ACTION_MESSAGE_KEY;
        List<String> scope = (List<String>) findScope(request, key);
        if (scope == null) {
            scope = new ArrayList<String>();
            storeScope(request, key, scope);
        }

        return scope;
    }

    /**
     * Must be implemented by sub-classes to lookup the scope under the given key from the correct
     * JEE interface. If it doesn't exist, this method should NOT create the scope and store it.
     *
     * @param   request The request.
     * @param   key The key that the scope is stored under.
     * @return  The scope or null if it doesn't exist.
     */
    protected abstract Object findScope(HttpServletRequest request, String key);

    /**
     * Must be implemented by sub-classes to store the given scope under the given key into the
     * correct JEE interface.
     *
     * @param   request The request.
     * @param   key The key to store the scope under.
     * @param   scope The scope.
     */
    protected abstract void storeScope(HttpServletRequest request, String key, Object scope);
}