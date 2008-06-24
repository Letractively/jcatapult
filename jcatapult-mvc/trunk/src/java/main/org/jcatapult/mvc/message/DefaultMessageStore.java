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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.mvc.message.scope.Scope;
import org.jcatapult.mvc.message.scope.ScopeProvider;

import com.google.inject.Inject;

/**
 * <p>
 * This is the default message workflow implementation. It removes
 * all flash messages from the session and places them in the request.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultMessageStore implements MessageStore {
    private final ActionInvocationStore actionInvocationStore;
    private final MessageProvider messageProvider;
    private final ScopeProvider scopeProvider;

    @Inject
    public DefaultMessageStore(ActionInvocationStore actionInvocationStore, MessageProvider messageProvider,
            ScopeProvider scopeProvider) {
        this.actionInvocationStore = actionInvocationStore;
        this.messageProvider = messageProvider;
        this.scopeProvider = scopeProvider;
    }

    /**
     * {@inheritDoc}
     */
    public void addConversionError(String field, String bundle, Map<String, String> attributes, Object... values)
    throws MissingMessageException {
        String key = field + ".conversionError";
        String message = messageProvider.getMessage(bundle, key, attributes, (Object[]) values);
        Scope scope = scopeProvider.lookup(MessageScope.REQUEST);
        scope.addFieldMessage(MessageType.ERROR, field, message);
    }

    /**
     * {@inheritDoc}
     */
    public void addFieldMessage(MessageScope scope, String field, String bundle, String key, Object... values)
    throws MissingMessageException {
        String message = messageProvider.getMessage(bundle, key, (Object[]) values);
        Scope s = scopeProvider.lookup(scope);
        s.addFieldMessage(MessageType.PLAIN, field, message);
    }

    /**
     * {@inheritDoc}
     */
    public void addFieldMessage(MessageScope scope, String field, String key, Object... values)
    throws MissingMessageException {
        ActionInvocation actionInvocation = actionInvocationStore.get();
        if (actionInvocation.action() == null) {
            throw new IllegalStateException("Attempting to add an field message without a bundle name " +
                "but the current request URL is not associated with an action class");
        }

        addFieldMessage(scope, field, actionInvocation.action().getClass().getName(), key, values);
    }

    /**
     * {@inheritDoc}
     */
    public void addFieldError(MessageScope scope, String field, String bundle, String key, Object... values)
    throws MissingMessageException {
        String message = messageProvider.getMessage(bundle, key, (Object[]) values);
        Scope s = scopeProvider.lookup(scope);
        s.addFieldMessage(MessageType.ERROR, field, message);
    }

    /**
     * {@inheritDoc}
     */
    public void addFieldError(MessageScope scope, String field, String key, Object... values)
    throws MissingMessageException {
        ActionInvocation actionInvocation = actionInvocationStore.get();
        if (actionInvocation.action() == null) {
            throw new IllegalStateException("Attempting to add an field error without a bundle name " +
                "but the current request URL is not associated with an action class");
        }

        addFieldError(scope, field, actionInvocation.action().getClass().getName(), key, values);
    }

    /**
     * {@inheritDoc}
     */
    public void addActionMessage(MessageScope scope, String bundle, String key, Object... values)
    throws MissingMessageException {
        String message = messageProvider.getMessage(bundle, key, (Object[]) values);
        Scope s = scopeProvider.lookup(scope);
        s.addActionMessage(MessageType.PLAIN, message);
    }

    /**
     * {@inheritDoc}
     */
    public void addActionMessage(MessageScope scope, String key, Object... values)
    throws MissingMessageException {
        ActionInvocation actionInvocation = actionInvocationStore.get();
        if (actionInvocation.action() == null) {
            throw new IllegalStateException("Attempting to add an action message without a bundle name " +
                "but the current request URL is not associated with an action class");
        }

        addActionMessage(scope, actionInvocation.action().getClass().getName(), key, values);
    }

    /**
     * {@inheritDoc}
     */
    public void addActionError(MessageScope scope, String bundle, String key, Object... values)
    throws MissingMessageException {
        String message = messageProvider.getMessage(bundle, key, (Object[]) values);
        Scope s = scopeProvider.lookup(scope);
        s.addActionMessage(MessageType.ERROR, message);
    }

    /**
     * {@inheritDoc}
     */
    public void addActionError(MessageScope scope, String key, Object... values)
    throws MissingMessageException {
        ActionInvocation actionInvocation = actionInvocationStore.get();
        if (actionInvocation.action() == null) {
            throw new IllegalStateException("Attempting to add an action error without a bundle name " +
                "but the current request URL is not associated with an action class");
        }

        addActionError(scope, actionInvocation.action().getClass().getName(), key, values);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getActionMessages(MessageType type) {
        List<String> allMessages = new ArrayList<String>();
        List<Scope> allScopes = scopeProvider.getAllScopes();
        for (Scope scope : allScopes) {
            List<String> messages = scope.getActionMessages(type);
            if (messages != null) {
                allMessages.addAll(messages);
            }
        }

        return allMessages;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getFieldMessages(MessageType type) {
        Map<String, List<String>> allMessages = new HashMap<String, List<String>>();
        List<Scope> allScopes = scopeProvider.getAllScopes();
        for (Scope scope : allScopes) {
            Map<String, List<String>> messages = scope.getFieldMessages(type);
            if (messages != null) {
                allMessages.putAll(messages);
            }
        }

        return allMessages;
    }

    /**
     * {@inheritDoc}
     */
    public void clearActionMessages(MessageType type) {
        List<Scope> allScopes = scopeProvider.getAllScopes();
        for (Scope scope : allScopes) {
            scope.clearActionMessages(type);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearFieldMessages(MessageType type) {
        List<Scope> allScopes = scopeProvider.getAllScopes();
        for (Scope scope : allScopes) {
            scope.clearFieldMessages(type);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(MessageType type) {
        return !getActionMessages(type).isEmpty() || !getFieldMessages(type).isEmpty();
    }
}