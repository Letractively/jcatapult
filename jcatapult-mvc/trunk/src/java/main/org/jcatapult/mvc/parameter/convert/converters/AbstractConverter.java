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
package org.jcatapult.mvc.parameter.convert.converters;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.Converter;
import org.jcatapult.mvc.parameter.convert.ConverterStateException;

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
    /**
     * Handles the following cases:
     *
     * <ul>
     * <li>Null - returns null</li>
     * <li>Type is an array - calls stringToArray</li>
     * <li>Type is not an array and values length is 1 or 0 - calls stringToObject</li>
     * <li>Type is an array and values length is > 1 - calls stringsToArray</li>
     * <li>Type is not an array and values length is > 1 - calls stringsToObject</li>
     * </ul>
     *
     * @param   values The values to convert.
     * @param   convertTo The type to convert to.
     * @param   attributes The parameter attributes used to assist in conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    public Object convertFromStrings(String[] values, Type convertTo, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        // Handle null
        if (values == null) {
            return null;
        }

        // Handle a zero or one String
        Class<?> rawType = rawType(convertTo);
        if (values.length <= 1) {
            String value = (values.length == 1) ? values[0] : null;

            if (rawType.isArray()) {
                // Punt on multi-dimensional arrays
                if (rawType.getComponentType().isArray()) {
                    throw new ConverterStateException("Converter [" + getClass() + "] does not support" +
                        " conversion to multi-dimensional arrays of type [" + convertTo + "]");
                }

                return stringToArray(value, convertTo, attributes);
            }

            return stringToObject(value, convertTo, attributes);
        }

        // Handle multiple strings
        if (rawType.isArray()) {
            // Punt on multi-dimensional arrays
            if (rawType.getComponentType().isArray()) {
                throw new ConverterStateException("Converter [" + getClass() + "] does not support" +
                    " conversion to multi-dimensional arrays of type [" + convertTo + "]");
            }

            return stringsToArray(values, convertTo, attributes);
        }

        return stringsToObject(values, convertTo, attributes);
    }

    /**
     * Gets the raw type from a parameterized type or just casts the type to a Class.
     *
     * @param   type The type.
     * @return  The raw type.
     */
    protected Class<?> rawType(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }

        return (Class<?>) type;
    }

    /**
     * Gets the first parameter type is the given type is a parameterized type. If it isn't, this
     * returns null. If the type has multiple parameters, only the first is returned.
     *
     * @param   type The type.
     * @return  The first parameter type.
     */
    protected Class<?> parameterType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }

        return null;
    }

    /**
     * Converts the value to a String.
     *
     * @param   value The value to convert.
     * @param   convertFrom The original Type of the value.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    public String convertToString(Object value, Type convertFrom, Map<String, String> attributes)
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
        Class<?> rawType = rawType(convertFrom);
        if (rawType.isArray()) {
            return arrayToString(value, convertFrom, attributes);
        }

        return objectToString(value, convertFrom, attributes);
    }

    /**
     * This performs the conversion from a single String value to an array of the given type.
     *
     * @param   value The value to convert to an array.
     * @param   convertTo The array type to convert to.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    protected Object stringToArray(String value, Type convertTo, Map<String, String> attributes)
    throws ConversionException {
        if (value == null) {
            return null;
        }

        Object finalArray;
        Class<?> rawType = rawType(convertTo);
        if (StringTools.isTrimmedEmpty(value)) {
            finalArray = Array.newInstance(rawType.getComponentType(), 0);
        } else {
            String[] parts = value.split(",");
            finalArray = Array.newInstance(rawType.getComponentType(), parts.length);
            for (int i = 0; i < parts.length; i++) {
                Object singleValue = stringToObject(parts[i], rawType.getComponentType(),
                    attributes);
                Array.set(finalArray, i, singleValue);
            }
        }

        return finalArray;
    }

    /**
     * This performs the conversion from an array of String values to an array of the given type.
     *
     * @param   values The values to convert to an array.
     * @param   convertTo The array type to convert to.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    protected Object stringsToArray(String[] values, Type convertTo, Map<String, String> attributes)
    throws ConversionException {
        if (values == null) {
            return null;
        }

        Object finalArray;
        Class<?> rawType = rawType(convertTo);
        if (values.length == 0) {
            finalArray = Array.newInstance(rawType.getComponentType(), 0);
        } else {
            finalArray = Array.newInstance(rawType.getComponentType(), values.length);
            for (int i = 0; i < values.length; i++) {
                Object singleValue = stringToObject(values[i], rawType.getComponentType(),
                    attributes);
                Array.set(finalArray, i, singleValue);
            }
        }

        return finalArray;
    }

    /**
     * This performs the conversion from an array to a single String value.
     *
     * @param   value The array value to convert to a String.
     * @param   convertFrom The array type to convert from.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    protected String arrayToString(Object value, Type convertFrom, Map<String, String> attributes)
    throws ConversionException {
        Class<?> rawType = rawType(convertFrom);
        if (!rawType.isArray()) {
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
            str.append(convertToString(o, value.getClass().getComponentType(), attributes));
            if (i + 1 < length) {
                str.append(",");
            }
        }

        return str.toString();
    }

    /**
     * Converts the single String value to an Object.
     *
     * @param   value The String value to convert.
     * @param   convertTo The type to convert to.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    protected abstract Object stringToObject(String value, Type convertTo, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;

    /**
     * Converts a String array to an Object.
     *
     * @param   values The String values to convert.
     * @param   convertTo The type to convert to.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    protected abstract Object stringsToObject(String[] values, Type convertTo, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;

    /**
     * Converts the Object value to a String.
     *
     * @param   value The Object value to convert.
     * @param   convertFrom The type to convert from.
     * @param   attributes The parameter attributes to assist in the conversion.
     * @return  The converted value.
     * @throws  ConversionException If the conversion failed.
     * @throws  ConverterStateException if the converter didn't have all of the information it needed
     *          to perform the conversion.
     */
    protected abstract String objectToString(Object value, Type convertFrom, Map<String, String> attributes)
    throws ConversionException, ConverterStateException;
}