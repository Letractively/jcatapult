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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.management.ReflectionException;

import net.java.lang.reflect.ReflectionTools;

/**
 * <p>
 * This class is the manager for all the TypeConverters. It loads the
 * type converters using a number of different mechanisms:
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
 * @since   1.0
 */
public class ConverterRegistry {
    private static Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    // Initialize the basic type converters including Number, String and Boolean
    static {
        Converter obj = new BooleanConverter();
        converters.put(Boolean.class, obj);
        converters.put(Boolean.TYPE, obj);

        obj = new NumberConverter();
        converters.put(Number.class, obj);
        converters.put(Byte.TYPE, obj);
        converters.put(Double.TYPE, obj);
        converters.put(Float.TYPE, obj);
        converters.put(Integer.TYPE, obj);
        converters.put(Long.TYPE, obj);
        converters.put(Short.TYPE, obj);

        obj = new CharacterConverter();
        converters.put(Character.class, obj);
        converters.put(Character.TYPE, obj);

//        try {
//            obj = (Converter) ReflectionTools.instantiate("net.java.convert.converters.StringConverter");
//            converters.put(String.class, obj);
//        } catch (ReflectionException re) {
//            System.err.println("WARNING - String type converter not found");
//        }
//
//        try {
//            obj = (Converter) ReflectionTools.instantiate("net.java.convert.converters.FileConverter");
//            converters.put(File.class, obj);
//        } catch (ReflectionException re) {
//            System.err.println("WARNING - File type converter not found");
//        }
    }

    /** Static class */
    private ConverterRegistry() {
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
    public static Converter lookup(Class<?> type) {
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

    /**
     * Registers the given converter with the given type
     *
     * @param   type The type to register the converter for
     * @param   converter The convert to register with the given type
     * @return  The converter that was already registered with the given type
     *          or null if this is the first converter being registered for
     *          that type
     */
    public static Converter register(Class<?> type, Converter converter) {
        return converters.put(type, converter);
    }

    /**
     * Unregisters the given converter with the given type
     *
     * @param   type The type to unregister the converter
     * @return  The converter that was registered with the given type or null
     *          if no converter was registered for the given type
     */
    public static Converter unregister(Class<?> type) {
        return converters.remove(type);
    }
}