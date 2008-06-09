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
package org.jcatapult.mvc.messages;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This is the default message workflow implementation. It removes
 * all flash messages from the session and places them in the request.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultMessageStore implements MessageWorkflow, MessageStore {
    public static final String FIELD_MESSAGES_KEY = "jcatapultFieldMessages";
    public static final String FIELD_ERRORS_KEY = "jcatapultFieldErrors";
    public static final String ACTION_MESSAGES_KEY = "jcatapultActionMessages";
    public static final String ACTION_ERRORS_KEY = "jcatapultActionErrors";
    public static final String FLASH_KEY = "jcatapultFlash";
    private final MessageProvider messageProvider;
    private final HttpServletRequest request;

    @Inject
    public DefaultMessageStore(HttpServletRequest request, MessageProvider messageProvider) {
        this.request = request;
        this.messageProvider = messageProvider;
    }

    //----------------------------- Store methods -----------------------------------

    /**
     * {@inheritDoc}
     */
    public void addConversionError(String field, String bundle, Locale locale, Map<String, String> attributes,
        String... values)
    throws MissingMessageException {
        field = field + ".conversionError";
        String message = messageProvider.getMessage(bundle, field, locale, attributes, values);
        if (message == null) {
            throw new MissingMessageException("A conversion message for the key [" + field +
                "] and locale [" + locale + "] could not be found.");
        }

        List<String> messages = getMessagesForField(field);
        messages.add(message);
    }

    //----------------------------- Workflow methods -----------------------------------

    /**
     * {@inheritDoc}
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            request.setAttribute(FLASH_KEY, session.getAttribute(FLASH_KEY));
            session.removeAttribute(FLASH_KEY);
        }

        chain.doWorkflow(request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }

    /**
     * Constructs or locates the List of messages for the given field.
     *
     * @param   field The field.
     * @return  The List of messages and never null.
     */
    @SuppressWarnings("unchecked")
    private List<String> getMessagesForField(String field) {
        Map<String, List<String>> fieldMessages = (Map<String, List<String>>) request.getAttribute(FIELD_KEY);
        if (fieldMessages == null) {
            fieldMessages = new HashMap<String, List<String>>();
            request.setAttribute(FIELD_KEY, fieldMessages);
        }

        List<String> messages = fieldMessages.get(field);
        if (messages == null) {
            messages = new ArrayList<String>();
            fieldMessages.put(field, messages);
        }

        return messages;
    }

    private class Flash {
        private Map<String, List<String>> fieldMessages = new HashMap<String, List<String>>();
        private Map<String, List<String>> fieldErrors = new HashMap<String, List<String>>();
        private List<String> actionMessages = new ArrayList<String>();
        private List<String> actionErrors = new ArrayList<String>();
    }
}