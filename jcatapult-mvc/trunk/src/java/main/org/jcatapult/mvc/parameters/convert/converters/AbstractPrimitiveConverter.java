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
package org.jcatapult.mvc.parameters.convert.converters;

import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.ConverterStateException;

/**
 * <p>
 * Overrides the abstract type converter to add abstract methods
 * for handling primitives.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractPrimitiveConverter extends AbstractConverter {
    protected <T> T stringToObject(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        if (value == null && convertTo.isPrimitive()) {
            return defaultPrimitive(convertTo, request, response, locale, attributes);
        } else if (value == null) {
            return null;
        }

        return stringToPrimitive(value, convertTo, request, response, locale, attributes);
    }

    protected <T> T stringsToObject(String[] values, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        throw new ConverterStateException("The primitive converter doesn't support String[] to Object conversion.");
    }

    protected <T> String objectToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return primitiveToString(value, convertFrom, request, response, locale, attributes);
    }

    /**
     * Returns the default primitive value for the given primitive type. This must use the wrapper
     * classes as return types.
     *
     * @param   convertTo The type of primitive to return the default value for.
     * @param   locale If needed.
     * @return  The wrapper that contains the default value for the primitive.
     * @throws  ConversionException If the default value could not be determined.
     */
    protected abstract <T> T defaultPrimitive(Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;

    /**
     * Converts the given String (always non-null) to a primitive denoted by the convertTo parameter.
     *
     * @param   value The value to convert.
     * @param   convertTo The type to convert the value to.
     * @param   request The servlet request.
     * @param   response The servlet response.
     * @param   locale The current locale.
     * @param   attributes Any attributes associated with the parameter being converted. Parameter
     *          attributes are described in the {@link ParameterService} class comment.
     * @return  The converted value.
     * @throws  ConversionException If there was a problem converting the given value to the
     *          given type.
     * @throws  ConverterStateException If the state of the request, response, locale or attributes
     *          was such that conversion could not occur. This is normally a fatal exception that is
     *          fixable during development but not in production.
     */
    protected abstract <T> T stringToPrimitive(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;

    /**
     * Converts the given String (always non-null) to a primitive denoted by the convertTo parameter.
     *
     * @param   value The Object value to convert.
     * @param   convertFrom The type to convert the value from.
     * @param   request The servlet request.
     * @param   response The servlet response.
     * @param   locale The current locale.
     * @param   attributes Any attributes associated with the parameter being converted. Parameter
     *          attributes are described in the {@link ParameterService} class comment.
     * @return  The converted value.
     * @throws  ConversionException If there was a problem converting the given value to the
     *          given type.
     * @throws  ConverterStateException If the state of the request, response, locale or attributes
     *          was such that conversion could not occur. This is normally a fatal exception that is
     *          fixable during development but not in production.
     */
    protected abstract <T> String primitiveToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;
}