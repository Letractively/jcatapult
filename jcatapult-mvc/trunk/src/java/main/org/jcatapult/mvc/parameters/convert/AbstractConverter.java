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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.java.lang.ObjectTools;
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
public abstract class AbstractConverter implements Converter {
    /**
     * <p>
     * This method is usually the only method that needs to be overridden in sub-classes. This is
     * where a String value is converted to the correct type. Normally, it is a good idea to check if
     * the convertTo type is an array and call {@link #convertStringToArray(String, String, String,
     * String, Class, Locale, Map)} method passing it the value, null for the delimiter and the
     * component type of the convertTo parameter.
     * </p>
     *
     * <p>
     * This method checks for an array and calls the convertStringToArray method and it also checks
     * for null and returns null. If these two cases aren't satisfied it checks that the implementation
     * supports the type given by calling the {@link #supportsConvertToType(Class)} and if not, it
     * throws a {@link ConversionException}. Lastly, this method will call the
     * {@link #stringToObject(String, Class, Locale, Map)} to convert the String to an Object.
     * Therefore, in most cases you only need to implement that method.
     * </p>
     *
     * @param   values The value(s) to convert. This might be a single value or multiple values.
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
    public <T> T convertFromStrings(String[] values, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        // Handle null
        if (values == null) {
            return null;
        }

        // Check support
        if (!supportsConvertToType(convertTo)) {
            throw new ConverterStateException("Converter [" + getClass() + "] does not support" +
                " conversion to type [" + convertTo + "]");
        }

        // Handle a single String
        if (values.length == 1) {
            String value = values[0];

            // Handles the case where the value is the same type or sub-type as the convertTo
            if (convertTo.isInstance(value)) {
                return (T) value;
            }

            if (convertTo.isArray()) {
                // Punt on multi-dimensional arrays
                if (convertTo.getComponentType().isArray()) {
                    throw new ConverterStateException("Converter [" + getClass() + "] does not support" +
                        " conversion to multi-dimensional arrays of type [" + convertTo + "]");
                }

                return stringToArray(values, convertTo, request, response, locale, attributes);
            }

            return stringToObject(values, convertTo, request, response, locale, attributes);
        }

        // Handle multiple strings

        // Handles the case where the value is the same type or sub-type as the convertTo
        if (convertTo.isInstance(values)) {
            return (T) values;
        }

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
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException {
        // Handle null
        if (value == null) {
            return null;

        }

        // Check support
        if (!supportsConvertToType(convertFrom)) {
            throw new ConversionException("Converter [" + getClass() + "] does not support" +
                " conversion from type [" + convertFrom + "]");
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

    /**
     * <p>
     * This method first checks if the value is null and if so it returns null.
     * </p>
     *
     * <p>
     * If the delimiter parameter is not empty or null it is used. Otherwise, a standard set of
     * delimiters are used (comma, semi-colon, tab, and new line).
     * </p>
     *
     * <p>
     * Using this delimiter, the string is tokenized and each token is passed to the convertString
     * method, along with the convertTo type. The result is added to a new array of the given convertTo
     * type or the convertTo type (if the convertTo type is an array type). This means that the order
     * of the tokens determines the order of the array elements and the array is as long as the number
     * of tokens.
     * </p>
     *
     * @param   value The string to convert to an array.
     * @param   delimiter The string to use as the delimiter when tokenizing.
     * @param   arrayStartSeparator Not used but given so that multi-dimensional arrays can be handled.
     * @param   arrayEndSeparator Not used but given so that multi-dimensional arrays can be handled.
     * @param   convertTo The type to convert the String to. Must be an array type.
     * @param   locale Not used.
     * @param   parameters Not used.
     * @return  The array generated by the conversion or null if the value is null.
     * @throws  ConversionException If a call to convertString throws an exception. Or if the
     *          convertTo parameter isn't an array or if the convertTo parameter is a multi-dimensional
     *          array. Currently, this class doesn't support multi-dimensional arrays.
     */
    public Object stringToArray(String value, String delimiter, String arrayStartSeparator,
        String arrayEndSeparator, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        assert (convertTo != null) : "convertTo == null";

        if (!convertTo.isArray()) {
            throw new ConversionException("The convertTo parameter must be an array type but is [" +
                convertTo + "]");
        }

        if (convertTo.getComponentType().isArray()) {
            throw new ConversionException("The AbstractConverter doesn't support " +
                "multi-dimensional arrays");
        }

        if (!supportsConvertToType(convertTo.getComponentType())) {
            throw new ConversionException("Converter [" + getClass() + "] does not support" +
                " conversion to type [" + convertTo.getComponentType() + "]");
        }

        if (value == null) {
            return null;
        }

        if (arrayStartSeparator == null) {
            arrayStartSeparator = DEFAULT_ARRAY_START;
        }

        if (arrayEndSeparator == null) {
            arrayEndSeparator = DEFAULT_ARRAY_END;
        }

        if (delimiter == null) {
            delimiter = DEFAULT_ARRAY_SEPARATOR;
        }

        if (value.startsWith(arrayStartSeparator) && value.endsWith(arrayEndSeparator)) {
            value = value.substring(arrayStartSeparator.length(), value.length() - arrayEndSeparator.length());
        }

        Object finalArray;
        if (StringTools.isTrimmedEmpty(value)) {
            finalArray = Array.newInstance(convertTo.getComponentType(), 0);
        } else {
            String[] values = value.split(delimiter);
            finalArray = Array.newInstance(convertTo.getComponentType(), values.length);
            for (int i = 0; i < values.length; i++) {
                Object singleValue = null;
                if (!values[i].equals(NULL_INDICATOR)) {
                    singleValue = convertString(values[i], convertTo.getComponentType(), locale, parameters);
                }

                Array.set(finalArray, i, singleValue);
            }
        }

        return finalArray;
    }

    /**
     * <p>
     * If the value is null, this returns null.
     * </p>
     *
     * <p>
     * If the array is length 1, than this returns just the toString of that single object in the
     * array (unless that object is null). If the array is null or length 0 or the value in the
     * array is null than null is returned.
     * </p>
     *
     * <p>
     * Otherwise, this iterates over the array and converts each value into a string and builds out
     * an array String using the parameters supplied.
     * </p>
     *
     * @param   value The array value to convert to a String.
     * @param   delimiter The array delimited to use.
     * @param   arrayStartSeparator (Optional) Can be used to delimit an array within a multi-dimensional
     *          array. This method will use this value to prepend the result, but does not support
     *          multi-dimensional arrays.
     * @param   arrayEndSeparator (Optional) Can be used to delimit an array within a multi-dimensional
     *          array. This method will use this value to append the result, but does not support
     *          multi-dimensional arrays.
     * @param   convertFrom (Optional) The convertFrom type in case the value is null.
     * @param   locale Not used.
     * @param   parameters Not used.
     * @return  The array as a String or the String <i>null</i> if the value is null.
     * @throws  ConversionException If the conversion failed. Or is the convertFrom parameter is
     *          given and not an array or a multi-dimensional array. Or if the value is given and it
     *          is not an array.
     */
    public String arrayToString(Object value, String delimiter, String arrayStartSeparator,
            String arrayEndSeparator, Class convertFrom, Locale locale, Map parameters)
    throws ConversionException {
        if (convertFrom != null && !convertFrom.isArray()) {
            throw new ConversionException("The convertFrom parameter must be an array type");
        }

        if (convertFrom != null && convertFrom.getComponentType().isArray()) {
            throw new ConversionException("The AbstractConverter does not support " +
                "multi-dimensional arrays");
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

        if (arrayStartSeparator == null) {
            arrayStartSeparator = DEFAULT_ARRAY_START;
        }

        if (arrayEndSeparator == null) {
            arrayEndSeparator = DEFAULT_ARRAY_END;
        }

        if (delimiter == null) {
            delimiter = DEFAULT_ARRAY_SEPARATOR;
        }

        int length = Array.getLength(value);
        StringBuffer str = new StringBuffer();
        str.append(arrayStartSeparator);
        for (int i = 0; i < length; i++) {
            Object o = Array.get(value, i);
            str.append(convertToString(o, value.getClass().getComponentType(), locale, parameters));
            if (i + 1 < length) {
                str.append(delimiter);
            }
        }
        str.append(arrayEndSeparator);

        return str.toString();
    }

    /**
     * Converts the given String value into an Object that is NOT a primitive. This will always be
     * called where the convertTo parameter is not a primitive type.
     *
     * @param   value The String to convert to an Object.
     * @param   convertTo The non-primitive type to convert to.
     * @param   locale If needed.
     * @param   parameters If needed.
     * @return  The value or null.
     * @throws  ConversionException If the conversion failed.
     */
    protected abstract Object stringToObject(String value, Class convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * Returns whether or not this Converter supports conversion from the given type.
     *
     * @param   type The type to check support for.
     * @return  True if this Converter supports it, false otherwise.
     */
    protected abstract boolean supportsConvertToType(Class type);
}