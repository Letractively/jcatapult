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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This is the message scope which fetches and stores values in the
 * HttpServletRequest.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class RequestScope extends AbstractJEEScope {
    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getFieldMessages(HttpServletRequest request, MessageType type, Object action) {
        FieldMessages messages = (FieldMessages) request.getAttribute(fieldKey(type));
        if (messages == null) {
            return Collections.emptyMap();
        }

        // Copy the map to protect it
        return new HashMap<String, List<String>>(messages);
    }

    /**
     * {@inheritDoc}
     */
    public void addFieldMessage(HttpServletRequest request, MessageType type, Object action, String fieldName, String message) {
        String key = fieldKey(type);
        FieldMessages messages = (FieldMessages) request.getAttribute(key);
        if (messages == null) {
            messages = new FieldMessages();
            request.setAttribute(key, messages);
        }

        messages.addMessage(fieldName, message);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getActionMessages(HttpServletRequest request, MessageType type, Object action) {
        List<String> messages = (List<String>) request.getAttribute(actionKey(type));
        if (messages == null) {
            return Collections.emptyList();
        }

        // Copy the map to protect it
        return new ArrayList<String>(messages);
    }

    /**
     * {@inheritDoc}
     */
    public void addActionMessage(HttpServletRequest request, MessageType type, Object action, String message) {
        String key = actionKey(type);
        List<String> messages = (List<String>) request.getAttribute(key);
        if (messages == null) {
            messages = new ArrayList<String>();
            request.setAttribute(key, messages);
        }

        messages.add(message);
    }

    /**
     * {@inheritDoc}
     */
    public void clear(HttpServletRequest request) {
        request.removeAttribute(ACTION_ERROR_KEY);
        request.removeAttribute(ACTION_MESSAGE_KEY);
        request.removeAttribute(FIELD_ERROR_KEY);
        request.removeAttribute(FIELD_MESSAGE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    public MessageScope scope() {
        return MessageScope.REQUEST;
    }
}