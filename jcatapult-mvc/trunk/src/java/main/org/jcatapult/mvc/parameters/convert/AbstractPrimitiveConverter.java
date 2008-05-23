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

import java.lang.reflect.Array;
import java.util.Locale;
import java.util.Map;

import net.java.lang.StringTools;

/**
 * <p>
 * Overrides the abstract type converter to add abstract methods
 * for handling primitives.
 * </p>
 *
 * @author  Brian Pontarelli
 * @since   1.0
 */
public abstract class AbstractPrimitiveConverter extends AbstractConverter {
    /**
     * If the value is null and the convertTo is a primitive, this calls the
     * {@link #defaultPrimitive(Class, Locale, Map)} method. Otherwise, it calls the convert method
     * in the super class ({@link AbstractConverter#convert(Object, Class, Locale, Map)}).
     */
    public Object convert(Object value, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        if (value == null && convertTo.isPrimitive()) {
            return defaultPrimitive(convertTo, locale, parameters);
        }

        return super.convert(value, convertTo, locale, parameters);
    }

    /**
     * If the String is null, empty, or simply whitespace and the convertTo is a primitive, this calls
     * the {@link #defaultPrimitive(Class, Locale, Map)} method. If not null, empty or whitespace and
     * convertTo is a primitive, this calls {@link #stringToPrimitive(String, Class, Locale, Map)}.
     * Otherwise, if the value is null, empty or whitespace and convertTo is not a primitive, this
     * returns null. For all other cases this calls
     * {@link AbstractConverter#convertString(String, Class, Locale, Map)}.
     */
    public Object convertString(String value, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        if (convertTo.isPrimitive()) {
            if (StringTools.isTrimmedEmpty(value)) {
                return defaultPrimitive(convertTo, locale, parameters);
            } else {
                return stringToPrimitive(value.trim(), convertTo, locale, parameters);
            }
        }

        if (value == null || StringTools.isTrimmedEmpty(value)) {
            return null;
        }

        return super.convertString(value.trim(), convertTo, locale, parameters);
    }

    /**
     * If the value is null and the convertFrom is a primitive, this throws a ConversionException.
     * Otherwise, this calls the super method
     * ({@link AbstractConverter#convertToString(Object, Class, Locale, Map)}).
     */
    public String convertToString(Object value, Class convertFrom, Locale locale, Map parameters)
    throws ConversionException {
        if (value == null && convertFrom.isPrimitive()) {
            throw new ConversionException("Cannot convert null primitives to Strings since Java " +
                "doesn't support null primitives. Did you mean to pass in a value or specify the " +
                "convertFrom as a wrapper?");
        }

        return super.convertToString(value, convertFrom, locale, parameters);
    }

    /**
     * If the values parameter is null or an array of zero length and the convertTo parameter is a
     * primitive, this calls the {@link #defaultPrimitive(Class, Locale, Map)} method. Otherwise, this
     * calls the super method ({@link AbstractConverter#convertArray(Object, Class, Locale, Map)}).
     */
    public Object convertArray(Object values, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        if (convertTo.isPrimitive() && (values == null ||
                (values.getClass().isArray() && Array.getLength(values) == 0))) {
            return defaultPrimitive(convertTo, locale, parameters);
        }

        return super.convertArray(values, convertTo, locale, parameters);
    }

    /**
     * Returns the default primitive value for the given primitive type. This must use the wrapper
     * classes as return types.
     *
     * @param   convertTo The type of primitive to return the default value for.
     * @param   locale If needed.
     * @param   parameters If needed.
     * @return  The wrapper that contains the default value for the primitive.
     * @throws  ConversionException If the default value could not be determined.
     */
    protected abstract Object defaultPrimitive(Class convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * Converts the given String (always non-null) to a primitive denoted by the convertTo parameter.
     *
     * @param   value The String value to convert to a primitive.
     * @param   convertTo The type of primitive to convert to.
     * @param   locale If needed.
     * @param   parameters If needed.
     * @return  The primitive value and never null (as a wrapper).
     * @throws  ConversionException If the conversion failed.
     */
    protected abstract Object stringToPrimitive(String value, Class convertTo, Locale locale, Map parameters)
    throws ConversionException;
}