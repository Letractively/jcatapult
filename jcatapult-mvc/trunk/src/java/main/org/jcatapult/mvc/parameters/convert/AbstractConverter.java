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
 * @since   1.0
 */
public abstract class AbstractConverter implements Converter {
    /**
     * <p>
     * This the value is null, this method calls the convertToString method passing all of the parameters
     * given. This allows that method to handle null values rather than this method.
     * </p>
     *
     * <p>
     * If the convertTo class is an array, this method calls convertToArray passing it the value and
     * the convertTo type, which is the type given (i.e. the array type and NOT the component type).
     * </p>
     *
     * <p>
     * If the value is an instanceof an array, this method calls the convertArray method passing it
     * the value and the convertTo type. Since that method also handles arrays of primitives, no cast
     * is performed.
     * </p>
     *
     * <p>
     * Otherwise, this method calls convertString passing it the value by calling the toString method
     * and the other parameters.
     * </p>
     *
     * <p>
     * Sub-classes should only override this method if they intend to handle conversion
     * between two Object types, both of which are NOT String. For example, if a
     * sub-class wished to convert Integer objects to Long objects, it could do that
     * in this method. It is a good idea however, that sub-classes call this
     * implementation if they do not handle the conversion.
     * </p>
     *
     * @param   value The value to convert.
     * @param   convertTo The type to convert the value to.
     * @param   locale Not used but passed on.
     * @param   parameters Not used but passed on.
     * @return  The converted value or null if the value is null. This returns value if value is an
     *          instance of the convertTo type.
     * @throws  ConversionException If there was a problem converting the given value to the
     *          given type.
     */
    public Object convert(Object value, Class convertTo, Locale locale, Map parameters) throws ConversionException {
        if (value == null) {
            return convertString((String) value, convertTo, locale, parameters);
        }

        // Handles the case where the value is the same type or sub-type as the convertTo
        if (convertTo.isInstance(value)) {
            return value;
        }

        if (convertTo == String.class) {
            return convertToString(value, value.getClass(), locale, parameters);
        }

        if (convertTo.isArray()) {
            return convertToArray(value, convertTo, locale, parameters);
        }

        // This handles the case where the value is an array of an assignable type
        // with length one
        if (value.getClass().isArray()) {
            return convertArray(value, convertTo, locale, parameters);
        }

        return convertString(value.toString(), convertTo, locale, parameters);
    }

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
     * @param   value The String value to convert to the given type.
     * @param   convertTo The type to convert to.
     * @param   locale Not used but passed on.
     * @param   parameters Not used but passed on.
     * @return  Either the value parameter itself or an array version of the String value, if the
     *          convertTo type is an array. If the value is null, empty, or whitespace then this
     *          returns null.
     * @throws  ConversionException If the convertTo type is an array and the convertStringToArray
     *          method throws an exception or if the implementation doesn't support the type given.
     */
    public Object convertString(String value, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        if (convertTo.isArray()) {
            return convertStringToArray(value, DEFAULT_ARRAY_SEPARATOR, DEFAULT_ARRAY_START,
                DEFAULT_ARRAY_END, convertTo, locale, parameters);
        } else if (!supportsConvertToType(convertTo)) {
            throw new ConversionException("Converter [" + getClass() + "] does not support" +
                " conversion to type [" + convertTo + "]");
        }

        if (value == null) {
            return null;
        }

        return stringToObject(value, convertTo, locale, parameters);
    }

    /**
     * <p>
     * This method first checks that either the value or the convertFrom parameter is non-null. If
     * they are both null, it will not be able to determine how to convert to a String. In this case
     * an exception is thrown.
     * </p>
     *
     * <p>
     * Next, this method checks if the value is null. If it is it returns null.
     * </p>
     *
     * <p>
     * Lastly, if the convertTo parameter or the value is an array, this method calls the
     * {@link Converter#convertArrayToString(Object,String,String,String,Class,Locale,Map)} method
     * with the parameters passed to this method. If not, if calls toString on the value.
     * </p>
     *
     * @param   value The value to convert to a String.
     * @param   convertFrom In case value is null.
     * @param   locale Not used but passed on.
     * @param   parameters Not used but passed on.
     * @return  The String or null if the value is null or an empty array.
     * @throws  ConversionException If the conversion fails or the value and the convertFrom parameters
     *          are null.
     */
    public String convertToString(Object value, Class convertFrom, Locale locale, Map parameters)
    throws ConversionException {
        if (value == null && convertFrom == null) {
            throw new ConversionException("The value and convertFrom parameters cannot both be null.");
        }

        if (value == null) {
            return null;
        }

        String str;
        if (value.getClass().isArray()) {
            str = convertArrayToString(value, DEFAULT_ARRAY_SEPARATOR, DEFAULT_ARRAY_START,
                DEFAULT_ARRAY_END, convertFrom, locale, parameters);
        } else {
            str = value.toString();
        }

        return str;
    }

    /**
     * <p>
     * This method first checks if the value is null. If the value is null it returns null. After
     * this check, it checks if the convertTo type parameter is an array. If so, this method calls
     * the {@link #convertArrayToArray(Object, Class, Locale, Map)} method with the parameters passed
     * to this method. If the value is an array of length 0, this returns null. If the array is an
     * array of length 1, it first checks if the array value at indices 0 is an instanceof the
     * convertTo type, it returns the value at indices 0. If not, it returns the result of calling
     * {@link #convert(Object, Class, Locale, Map)} with the value at indices 0 and the convertTo type.
     * </p>
     *
     * <p>
     * Otherwise, this method throws an exception because it is normally not needed to
     * convert an array to an object. If the array array is null, then this method
     * returns null.
     * </p>
     *
     * @param   array The array of array to convert to the given type
     * @param   convertTo The type to convert to
     * @param   locale Not used but passed on.
     * @param   parameters Not used but passed on.
     * @return  Either the result of calling the convertArrayToArray method if the convertTo type is
     *          an array, or the convert method if the array array is of length 1. If the array is
     *          null, then this returns null. If array length is zero, this returns null. If array is
     *          length 1 but the element at indices 0 is null, this returns null.
     * @throws  ConversionException If either the convertArray or convert method throw an exception
     *          (depending on which method is called) or always (see method comment).
     */
    public Object convertArray(Object array, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        // If the array is null, no use in converting it
        if (array == null) {
            return null;
        }

        if (convertTo.isArray()) {
            return convertArrayToArray(array, convertTo, locale, parameters);
        }

        if (!array.getClass().isArray()) {
            throw new ConversionException("The array parameter must be an array");
        }

        // If the array is length zero
        int length = Array.getLength(array);
        if (length == 0) {
            return null;
        }

        if (length == 1) {
            Object value = Array.get(array, 0);
            if (convertTo.isInstance(value)) {
                return value;
            }

            return convert(value, convertTo, locale, parameters);
        }

        throw new ConversionException("AbstractConverter does not handle" +
            " converting an array with size not equal to one to an object");
    }

    /**
     * <p>
     * This method checks if the value is null, or length 0. In either case it returns null.
     * Otherwise it calls the {@link #recurseArrayToArray(Object, Class, Locale, Map)} to handle
     * nested arrays and the parsing recursively.
     * </p>
     *
     * @param   array The array to convert to an array of the given type.
     * @param   convertTo The type to convert to an array of. if this parameter is an array.
     *          type, then this method converts to that type.
     *          the an array of the type is converted to.
     * @param   locale Not used but passed on.
     * @param   parameters Not used but passed on.
     * @return  The converted array or null if the array array is null or length 0.
     * @throws  ConversionException If the convert method throws a ConversionException
     *          when each of the positions in the array array are converted.
     */
    public Object convertArrayToArray(Object array, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        // If the value is null, no use in converting it
        if (array == null) {
            return null;
        }

        if (!array.getClass().isArray()) {
            throw new ConversionException("Array parameter must be an array");
        }

        if (!convertTo.isArray()) {
            throw new ConversionException("The array parameter must be an array and the convertTo" +
                "parameter must be an array type");
        }

        return recurseArrayToArray(array, convertTo, locale, parameters);
    }

    /**
     * <p>
     * Recursively deconstructs the given array and piece meal converts it to an array of the given
     * type. This is done using recursion to step over each array in a multi-dimensional array structure.
     * If at any point the array length is zero, this returns null and NOT a new array of the convertTo
     * to of length zero. This was a design decision and might not reflect the desired behavior, in
     * which case this method should be overridden.
     * </p>
     *
     * <p>
     * Once a single value is encountered in the recursion, this method calls the
     * {@link #convert(Object, Class, Locale, Map)} method passing in the component type to convert
     * to. For example, if convertTo is String[][], then convert is called with convertTo equal to
     * String.
     * </p>
     *
     * <p>
     * If the dimensions of the convertTo parameter is not the same as the array parameter in terms
     * of array depth, this method throws and exception.
     * </p>
     *
     * @param   array The array to convert. Can be empty, single or multi-dimensional.
     * @param   convertTo The type of array to convert the given array to.
     * @param   locale Not used but supplied for overridding and passed on.
     * @param   parameters Not used but supplied for overridding and passed on.
     * @return  The converted array.
     * @throws  ConversionException If the component type is not supported by this implementation
     *          or the dimensions are off or the conversion of a single value fails.
     */
    protected Object recurseArrayToArray(Object array, Class convertTo, Locale locale, Map parameters) {
        int length = Array.getLength(array);
        if (length == 0) {
            return null;
        }

        Class componentType = convertTo.getComponentType();
        if (!componentType.isArray() && !supportsConvertToType(componentType)) {
            throw new ConversionException("Converter [" + getClass() + "] does not support" +
                " conversion to type [" + convertTo + "]");
        }

        Object result = Array.newInstance(componentType, length);
        if (componentType.isArray()) {
            if (!array.getClass().getComponentType().isArray()) {
                throw new ConversionException("array parameter and convertTo parameter must be " +
                    "the same number of dimensions.");
            }

            for (int i = 0; i < length; i++) {
                Object value = Array.get(array, i);
                if (value == null || !convertTo.isInstance(value)) {
                    value = recurseArrayToArray(value, componentType, locale, parameters);
                }

                Array.set(result, i, value);
            }
        } else {
            if (array.getClass().getComponentType().isArray()) {
                throw new ConversionException("array parameter and convertTo parameter must be " +
                    "the same number of dimensions.");
            }

            for (int i = 0; i < length; i++) {
                Object value = Array.get(array, i);
                if (value == null || !convertTo.isInstance(value)) {
                    value = convert(value, componentType, locale, parameters);
                }

                Array.set(result, i, value);
            }
        }

        return result;
    }

    /**
     * <p>
     * This method checks if the value is null and if so returns null.
     * </p>
     *
     * <p>
     * If the value is an instanceof an array, then this method calls the
     * {@link #convertArrayToArray(Object, Class, Locale, Map)} method. Otherwise,
     * {@link #convertStringToArray(String, String, String, String, Class, Locale, Map)} is called
     * with the value converted to a String using the toString method, the convertTo type and null
     * for the delimiter (see convertStringToArray for an explaination of this parameter).
     * </p>
     *
     * @param   value The value to convert to an array of the given type or simply the given type if
     *          the given type is an array.
     * @param   convertTo The type to convert to an array of. If this parameter is an array type,
     *          this method converts to that type.
     * @param   locale Not used.
     * @param   parameters Not used.
     * @return  The value converted to an array or null if the value is null.
     * @throws  ConversionException If convertArrayToArray or convertStringToArray throw a
     *          ConversionException (depending on which one is called). Or if the convertTo
     *          parameter isn't an array or if the convertTo parameter is a multi-dimensional array.
     *          Currently, this class doesn't support multi-dimensional arrays.
     */
    public Object convertToArray(Object value, Class convertTo, Locale locale, Map parameters)
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

        // If the value is null, no use in converting it
        if (value == null) {
            return null;
        }

        if (value.getClass().isArray()) {
            return convertArrayToArray(value, convertTo, locale, parameters);
        }

        if (ObjectTools.isCollection(value)) {
            return ObjectTools.makeArray(value);
        }

        return convertStringToArray(value.toString(), DEFAULT_ARRAY_SEPARATOR, DEFAULT_ARRAY_START,
            DEFAULT_ARRAY_END, convertTo, locale, parameters);
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
    public Object convertStringToArray(String value, String delimiter, String arrayStartSeparator,
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
    public String convertArrayToString(Object value, String delimiter, String arrayStartSeparator,
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