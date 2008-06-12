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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;

/**
 * <p>
 * This class implements the scope registry.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultScopeRegistry implements ScopeRegistry {
    private static final Logger logger = Logger.getLogger(org.jcatapult.mvc.scope.DefaultScopeRegistry.class.getName());
    private Map<MessageScope, Scope> scopes = new HashMap<MessageScope, Scope>();

    @Inject
    public DefaultScopeRegistry(Injector injector) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Key<?> key : bindings.keySet()) {
            Type scopeType = key.getTypeLiteral().getType();
            if (scopeType instanceof Class && Scope.class.isAssignableFrom((Class<?>) scopeType)) {
                Scope scope = (Scope) bindings.get(key).getProvider().get();
                MessageScope messageScope = scope.scope();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Registering scope class [" + scope.getClass() + "] for type [" + messageScope + "]");
                }

                scopes.put(messageScope, scope);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Scope lookup(MessageScope scope) {
        return scopes.get(scope);
    }

    /**
     * {@inheritDoc}
     */
    public List<Scope> getAllScopes() {
        return new ArrayList<Scope>(scopes.values());
    }
}