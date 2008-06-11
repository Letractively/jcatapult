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
package org.jcatapult.mvc.action.result;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcatapult.mvc.parameter.convert.ConverterRegistry;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;

/**
 * <p>
 * This class is the manager for all the Results. It loads the
 * Results from Guice. Therefore, if you want to supply a custom
 * Result, just add it to a Guice module and place it in your
 * classpath. JCatapult will discover the module and load it up.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultResultRegistry implements ResultRegistry {
    private static final Logger logger = Logger.getLogger(ConverterRegistry.class.getName());
    private Map<Class<? extends Annotation>, Result> results = new HashMap<Class<? extends Annotation>, Result>();

    @Inject
    public DefaultResultRegistry(Injector injector) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Key<?> key : bindings.keySet()) {
            Type bindingType = key.getTypeLiteral().getType();
            if (bindingType instanceof Class && Result.class.isAssignableFrom((Class<?>) bindingType)) {
                Result result = (Result) bindings.get(key).getProvider().get();
                Class<? extends Annotation> annotation = result.annotationType();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Registering result class [" + result.getClass() + "] for annotationType [" + annotation + "]");
                }

                results.put(annotation, result);
            }
        }
    }

    /**
     * <p>
     * Returns the result for the given annotation.
     * </p>
     *
     * @param   annotation The annotation.
     * @return  The Result or null if one was not found
     */
    public Result lookup(Class<? extends Annotation> annotation) {
        return results.get(annotation);
    }
}