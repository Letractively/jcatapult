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
package org.jcatapult.mvc.messages.scope;

import java.util.ArrayList;
import java.util.Collections;
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
public abstract class AbstractScope implements Scope {
    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getFieldMessages(HttpServletRequest request, MessageType type, Object action) {
        return Collections.unmodifiableMap(getFieldScope(request, type, action));
    }

    /**
     * {@inheritDoc}
     */
    public void setFieldMessages(HttpServletRequest request, MessageType type, Object action, String fieldName, String message) {
        Map<String, List<String>> map = getFieldScope(request, type, action);
        List<String> list = map.get(fieldName);
        if (list == null) {
            list = new ArrayList<String>();
            map.put(fieldName, list);
        }

        list.add(message);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getActionMessages(HttpServletRequest request, MessageType type, Object action) {
        return Collections.unmodifiableList(getActionScope(request, type, action));
    }

    /**
     * {@inheritDoc}
     */
    public void setActionMessages(HttpServletRequest request, MessageType type, Object action, String message) {
        List<String> list = getActionScope(request, type, action);
        list.add(message);
    }

    /**
     * Implemented by sub-classes to fetch the Map of field messages from the scope.
     *
     * @param   request The request.
     * @param   type The message type.
     * @param   action The action.
     * @return  The Map and never null.
     */
    protected abstract Map<String, List<String>> getFieldScope(HttpServletRequest request, MessageType type, Object action);

    /**
     * Implemented by sub-classes to fetch the List of action messages from the scope.
     *
     * @param   request The request.
     * @param   type The message type.
     * @param   action The action.
     * @return  The List and never null.
     */
    protected abstract List<String> getActionScope(HttpServletRequest request, MessageType type, Object action);
}