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
 *
 */
package org.jcatapult.mvc.parameters.convert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * <p>
 * This class is the manager for all the Converters. It loads the
 * type converters from Guice. Therefore, if you want to supply a custom
 * type converter, just add it to a Guice module and place it in your
 * classpath. JCatapult will discover the module and load it up.
 * </p>
 *
 * <p>
 * A converter for a given type will be retrieved when the manager
 * is queried for that type and all sub class of that type, unless
 * another converter is registered for a sub class of the type. For
 * example, registering a convert for the type Number would ensure
 * that Integer, Double, Float, etc. used that converter for
 * conversions. If a converter was registered for Number and another
 * converter for Double, the converter for Number would handle all
 * sub-class of Number (Integer, etc.) except Double.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultConverterRegistry implements ConverterRegistry {
    private static final Logger logger = Logger.getLogger(ConverterRegistry.class.getName());
    private Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    @Inject
    public DefaultConverterRegistry(Injector injector) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Key<?> key : bindings.keySet()) {
            Type bindingType = key.getTypeLiteral().getType();
            if (bindingType instanceof Class && Converter.class.isAssignableFrom((Class<?>) bindingType)) {
                Converter converter = (Converter) bindings.get(key).getProvider().get();
                Class<?>[] types = converter.supportedTypes();
                for (Class<?> type : types) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Registering converter class [" + converter.getClass() + "] for type [" + type + "]");
                    }

                    converters.put(type, converter);
                }
            }
        }
    }

    /**
     * <p>
     * Returns the type converter for the given type. This converter is either
     * the converter associated with the given type of associated with a super
     * class of the given type (not interfaces). This principal also works with
     * arrays. If the type is an array, then what happens is that the array type
     * is asked for its component type using the method getComponentType and this
     * type is used to query the manager. So, the converter registered for Number
     * is returned Double[] is queried (because Double is queried and since no
     * converter was register for it, then Number is checked).
     * </p>
     *
     * <p>
     * Normal types work the exact same way. First the type is checked and then
     * its parents are checked until Object is reached, in which case null is
     * returned.
     * </p>
     *
     * <p>
     * Primitive values are treated as their wrapper classes. So, if int.class
     * is passed into this method (queried) then either a converter registered
     * for Integer, or Number or null is returned depending on what converters
     * have been registered so far.
     * </p>
     *
     * @param   type The type to start with when looking for converters
     * @return  The converter or null if one was not found
     */
    public Converter lookup(Class<?> type) {
        Class localType = type;
        Converter converter = null;

        // If it is an array, just use the component type because TypeConverters
        // can convert to arrays
        while (localType.isArray()) {
            localType = localType.getComponentType();
        }

        // The local type becomes null when it is an interface or a primitive and the
        // super class is asked for
        while (localType != null && localType != Object.class) {
            converter = converters.get(localType);
            if (converter == null) {
                localType = localType.getSuperclass();
            } else {
                break;
            }
        }

        // I'm not sure about this yet
        /*
        if (converter == null) {
            Class [] interfaces = type.getInterfaces();
            Converter interfaceConverter;

            for (int i = 0; i < interfaces.length; i++) {
                interfaceConverter = getTypeConverter(interfaces[i]);
                if (interfaceConverter != null) {
                    break;
                }
            }
        }
        */

        return converter;
    }
}