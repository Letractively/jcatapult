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
package org.jcatapult.mvc.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.message.scope.FlashScope;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.mvc.message.scope.Scope;
import org.jcatapult.mvc.message.scope.ScopeRegistry;
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
    private final MessageProvider messageProvider;
    private final ScopeRegistry scopeRegistry;
    private final FlashScope flashScope;

    @Inject
    public DefaultMessageStore(MessageProvider messageProvider, ScopeRegistry scopeRegistry,
            FlashScope flashScope) {
        this.messageProvider = messageProvider;
        this.scopeRegistry = scopeRegistry;
        this.flashScope = flashScope;
    }

    //----------------------------- Store methods -----------------------------------

    /**
     * {@inheritDoc}
     */
    public void addConversionError(HttpServletRequest request, Object action, String field, String bundle,
            Locale locale, Map<String, String> attributes, String... values)
    throws MissingMessageException {
        field = field + ".conversionError";
        String message = messageProvider.getMessage(bundle, field, locale, attributes, values);
        if (message == null) {
            throw new MissingMessageException("A conversion message for the key [" + field +
                "] and locale [" + locale + "] could not be found.");
        }

        Scope scope = scopeRegistry.lookup(MessageScope.REQUEST);
        scope.addFieldMessage(request, MessageType.ERROR, action, field, message);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getActionMessages(HttpServletRequest request, MessageType type, Object action) {
        List<String> allMessages = new ArrayList<String>();
        List<Scope> allScopes = scopeRegistry.getAllScopes();
        for (Scope scope : allScopes) {
            List<String> messages = scope.getActionMessages(request, type, action);
            if (messages != null) {
                allMessages.addAll(messages);
            }
        }

        return allMessages;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getFieldMessages(HttpServletRequest request, MessageType type, Object action) {
        Map<String, List<String>> allMessages = new HashMap<String, List<String>>();
        List<Scope> allScopes = scopeRegistry.getAllScopes();
        for (Scope scope : allScopes) {
            Map<String, List<String>> messages = scope.getFieldMessages(request, type, action);
            if (messages != null) {
                allMessages.putAll(messages);
            }
        }

        return allMessages;
    }

    //----------------------------- Workflow methods -----------------------------------

    /**
     * {@inheritDoc}
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        flashScope.transferFlash(request);
        chain.doWorkflow(request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }
}