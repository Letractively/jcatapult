/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.guice;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * <p>
 * This class provides helper functions for Guice.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class GuiceTools {
    /**
     * Locates all of the Keys for the given Type.
     *
     * @param   injector The injector to locate the Keys inside.
     * @param   type The type.
     * @param   <T> The type.
     * @return  The list of keys or an empty list if there aren't any.
     */
    public static <T> List<Key<T>> getKeys(Injector injector, Class<T> type) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        List<Key<T>> results = new LinkedList<Key<T>>();
        for (Key<?> key : bindings.keySet()) {
            Class<?> bindingType = (Class<?>) key.getTypeLiteral().getType();
            if (type.isAssignableFrom(bindingType)) {
                results.add((Key<T>) key);
            }
        }
        return results;
    }
}
