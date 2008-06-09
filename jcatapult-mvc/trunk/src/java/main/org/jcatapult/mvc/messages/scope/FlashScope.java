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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This is the flash scope which stores messages in the HttpSession
 * under the flash key. It fetches values from the HttpServletRequest
 * under the same key as well as the HttpSession under that key. This
 * allows for flash messages to be migrated from the session to the request
 * during request handling so that they are not persisted in the session
 * forever. However, it also allows flash values to be retrieved during the
 * initial request from the session.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class FlashScope implements Scope {
    /**
     * The flash key used to store the field errors in the flash.
     */
    public static final String FLASH_FIELD_ERRORS_KEY = "jcatapultFlashFieldErrors";

    /**
     * The flash key used to store the field messages in the flash.
     */
    public static final String FLASH_FIELD_MESSAGES_KEY = "jcatapultFlashFieldMessages";

    /**
     * The flash key used to store the action errors in the flash.
     */
    public static final String FLASH_ACTION_ERRORS_KEY = "jcatapultFlashActionErrors";

    /**
     * The flash key used to store the action messages in the flash.
     */
    public static final String FLASH_ACTION_MESSAGES_KEY = "jcatapultFlashActionMessages";

    /**
     * This combines the flash messages from the request and from the session into a single Map. This
     * allows access to newly added flash messages as well as flash messages from the previous request
     * that have been transfered to the request.
     *
     * @param   request The request.
     * @param   type The type.
     * @param   action The action.
     * @return  The flash field messages.
     */
    public Map<String, List<String>> getFieldMessages(HttpServletRequest request, MessageType type, Object action) {
        String key = (type == MessageType.ERROR) ? FLASH_FIELD_ERRORS_KEY : FLASH_FIELD_MESSAGES_KEY;
        Map<String, List<String>> fromRequest = (Map<String, List<String>>) request.getAttribute(key);
        Map<String, List<String>> fromSession = (Map<String, List<String>>) request.getSession().getAttribute(key);
        Map<String, List<String>> combined = new HashMap<String, List<String>>();
        if (fromRequest != null && fromRequest.size() > 0) {
            combined.putAll(fromRequest);
        }
        if (fromSession != null && fromSession.size() > 0) {
            combined.putAll(fromSession);
        }

        return combined;
    }

    /**
     * Stores new messages in the flash scope in the session.
     *
     * @param   request The request used to get the session.
     * @param   type The type of message to store.
     * @param   action The action (not used).
     * @param   fieldName The name of the field that the message associated with.
     * @param   message The message itself.
     */
    public void setFieldMessages(HttpServletRequest request, MessageType type, Object action, String fieldName, String message) {
        String key = (type == MessageType.ERROR) ? FLASH_FIELD_ERRORS_KEY : FLASH_FIELD_MESSAGES_KEY;
        Map<String, List<String>> scope = (Map<String, List<String>>) request.getSession().getAttribute(key);
        if (scope == null) {
            scope = new HashMap<String, List<String>>();
            request.getSession().setAttribute(key, scope);
        }

        List<String> messages = scope.get(fieldName);
        if (message == null) {
            messages = new ArrayList<String>();
            scope.put(fieldName, messages);
        }

        messages.add(message);
    }

    /**
     * This combines the flash messages from the request and from the session into a single List. This
     * allows access to newly added flash messages as well as flash messages from the previous request
     * that have been transfered to the request.
     *
     * @param   request The request.
     * @param   type The type.
     * @param   action The action.
     * @return  The flash action messages.
     */
    public List<String> getActionMessages(HttpServletRequest request, MessageType type, Object action) {
        String key = (type == MessageType.ERROR) ? FLASH_ACTION_ERRORS_KEY : FLASH_ACTION_MESSAGES_KEY;
        List<String> fromRequest = (List<String>) request.getAttribute(key);
        List<String> fromSession = (List<String>) request.getSession().getAttribute(key);
        List<String> combined = new ArrayList<String>();
        if (fromRequest != null && fromRequest.size() > 0) {
            combined.addAll(fromRequest);
        }
        if (fromSession != null && fromSession.size() > 0) {
            combined.addAll(fromSession);
        }

        return combined;
    }

    /**
     * Stores new messages in the flash scope in the session.
     *
     * @param   request The request used to get the session.
     * @param   type The type of message to store.
     * @param   action The action (not used).
     * @param   message The message itself.
     */
    public void setActionMessages(HttpServletRequest request, MessageType type, Object action, String message) {
        String key = (type == MessageType.ERROR) ? FLASH_ACTION_ERRORS_KEY : FLASH_ACTION_MESSAGES_KEY;
        List<String> scope = (List<String>) request.getSession().getAttribute(key);
        if (scope == null) {
            scope = new ArrayList<String>();
            request.getSession().setAttribute(key, scope);
        }

        scope.add(message);
    }

    /**
     * Moves the flash from the session to the request.
     *
     * @param   request The request used to get the session and possibly store the flash.
     */
    public void transferFlash(HttpServletRequest request) {
        transferFlash(request, FLASH_FIELD_ERRORS_KEY);
        transferFlash(request, FLASH_FIELD_MESSAGES_KEY);
        transferFlash(request, FLASH_ACTION_ERRORS_KEY);
        transferFlash(request, FLASH_ACTION_MESSAGES_KEY);
    }

    /**
     * Transfers the value from the session under the given key to the request under the given key.
     *
     * @param   request The request.
     * @param   key The key.
     */
    protected void transferFlash(HttpServletRequest request, String key) {
        Map<String, Object> flash = (Map<String, Object>) request.getSession().getAttribute(key);
        if (flash != null) {
            request.getSession().removeAttribute(key);
            request.setAttribute(key, flash);
        }
    }

    /**
     * {@inheritDoc}
     */
    public MessageScope scope() {
        return MessageScope.FLASH;
    }
}