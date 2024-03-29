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
package org.jcatapult.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * <p>
 * This is the default implementation of the ObjectFactory that uses Guice.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class GuiceObjectFactory implements ObjectFactory {
    private final Injector injector;

    @Inject
    public GuiceObjectFactory(Injector injector) {
        this.injector = injector;
    }

    /**
     * Creates the class using Guice from the Injector from Guice.
     *
     * @param   klass The class to create.
     * @return  The Object and never null.
     */
    public <T> T create(Class<T> klass) {
        return injector.getInstance(klass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> getAllForType(Class<T> type) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        List<Class<? extends T>> results = new ArrayList<Class<? extends T>>();
        for (Key<?> key : bindings.keySet()) {
            Class<?> bindingType = (Class<?>) key.getTypeLiteral().getType();
            if (type.isAssignableFrom(bindingType)) {
                results.add((Class<? extends T>) bindingType);
            }
        }

        return results;
    }
}