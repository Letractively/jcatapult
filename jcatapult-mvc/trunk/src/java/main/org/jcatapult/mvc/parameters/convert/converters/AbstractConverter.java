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
package org.jcatapult.mvc.parameters.convert.converters;

import java.lang.reflect.Array;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameters.convert.Converter;
import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.ConverterStateException;

import net.java.lang.StringTools;

/**
 * <p>
 * This class is the base type converter for all the type converters
 * that handle Object types. If you are writing a converter for
 * primitive types, use the {@link AbstractPrimitiveConverter}
 * class.
 * </p>
 *
 * <p>
 * This class mostly delegates between the various method calls by
 * passing in default values or performing casting. Each method
 * describes how it functions.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public abstract class AbstractConverter implements Converter {
    public <T> T convertFromStrings(String[] values, Class<T> convertTo, HttpServletRequest request,
        Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        // Handle null
        if (values == null) {
            return null;
        }

        // Handle a zero or one String
        if (values.length <= 1) {
            String value = (values.length == 1) ? values[0] : null;

            if (convertTo.isArray()) {
                // Punt on multi-dimensional arrays
                if (convertTo.getComponentType().isArray()) {
                    throw new ConverterStateException("Converter [" + getClass() + "] does not support" +
                        " conversion to multi-dimensional arrays of type [" + convertTo + "]");
                }

                return stringToArray(value, convertTo, request, response, locale, attributes);
            }

            return stringToObject(value, convertTo, request, response, locale, attributes);
        }

        // Handle multiple strings
        if (convertTo.isArray()) {
            // Punt on multi-dimensional arrays
            if (convertTo.getComponentType().isArray()) {
                throw new ConverterStateException("Converter [" + getClass() + "] does not support" +
                    " conversion to multi-dimensional arrays of type [" + convertTo + "]");
            }

            return stringsToArray(values, convertTo, request, response, locale, attributes);
        }

        return stringsToObject(values, convertTo, request, response, locale, attributes);
    }

    public <T> String convertToString(T value, Class<T> convertFrom, HttpServletRequest request,
        Locale locale, Map<String, String> attributes)
    throws ConversionException {
        // Handle null
        if (value == null) {
            return null;
        }

        // Check simple conversion
        if (value instanceof String) {
            return (String) value;
        }

        // Handle arrays
        if (convertFrom.isArray()) {
            return arrayToString(value, convertFrom, request, response, locale, attributes);
        }

        return objectToString(value, convertFrom, request, response, locale, attributes);
    }

    protected <T> T stringToArray(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException {
        if (value == null) {
            return null;
        }

        Object finalArray;
        if (StringTools.isTrimmedEmpty(value)) {
            finalArray = Array.newInstance(convertTo.getComponentType(), 0);
        } else {
            String[] parts = value.split(",");
            finalArray = Array.newInstance(convertTo.getComponentType(), parts.length);
            for (int i = 0; i < parts.length; i++) {
                Object singleValue = stringToObject(parts[i], convertTo.getComponentType(), request,
                    response, locale, attributes);
                Array.set(finalArray, i, singleValue);
            }
        }

        return (T) finalArray;
    }

    protected <T> T stringsToArray(String[] values, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException {
        if (values == null) {
            return null;
        }

        Object finalArray;
        if (values.length == 0) {
            finalArray = Array.newInstance(convertTo.getComponentType(), 0);
        } else {
            finalArray = Array.newInstance(convertTo.getComponentType(), values.length);
            for (int i = 0; i < values.length; i++) {
                Object singleValue = stringToObject(values[i], convertTo.getComponentType(), request,
                    response, locale, attributes);
                Array.set(finalArray, i, singleValue);
            }
        }

        return (T) finalArray;
    }

    public <T> String arrayToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException {
        if (convertFrom != null && !convertFrom.isArray()) {
            throw new ConversionException("The convertFrom parameter must be an array type");
        }

        if (value == null) {
            return null;
        }

        if (!value.getClass().isArray()) {
            throw new ConversionException("The value is not an array");
        }

        if (value.getClass().getComponentType().isArray()) {
            throw new ConversionException("The value is a multi-dimensional array, which is not" +
                " supported by the AbstractConverter");
        }

        int length = Array.getLength(value);
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < length; i++) {
            Object o = Array.get(value, i);
            str.append(convertToString(o, (Class<Object>) value.getClass().getComponentType(),
                request, locale, attributes));
            if (i + 1 < length) {
                str.append(",");
            }
        }

        return str.toString();
    }

    protected abstract <T> T stringToObject(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;

    protected abstract <T> T stringsToObject(String[] values, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;

    protected abstract <T> String objectToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;
}