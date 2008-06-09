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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcatapult.mvc.scope.Scope;
import org.jcatapult.mvc.scope.ScopeRegistry;

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
    private Map<Class<? extends Annotation>, Scope> scopes = new HashMap<Class<? extends Annotation>, Scope>();

    @Inject
    public DefaultScopeRegistry(Injector injector) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Key<?> key : bindings.keySet()) {
            Type scopeType = key.getTypeLiteral().getType();
            if (scopeType instanceof Class && Scope.class.isAssignableFrom((Class<?>) scopeType)) {
                Scope scope = (Scope) bindings.get(key).getProvider().get();
                Class<? extends Annotation> type = scope.annotationType();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Registering scope class [" + scope.getClass() + "] for type [" + type + "]");
                }

                scopes.put(type, scope);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Scope lookup(Class<? extends Annotation> scopeAnnotation) {
        return scopes.get(scopeAnnotation);
    }
}