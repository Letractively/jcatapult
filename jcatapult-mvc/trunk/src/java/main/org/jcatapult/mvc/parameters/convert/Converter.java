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

import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * TypeConverters are used to convert objects from one type
 * to another, convert strings to objects and to convert from
 * objects and strings to arrays of objects. The way that
 * this is setup is that a Converter implementation is
 * registered with the ConverterRegistry. The manager can
 * then be queried for a particular TypeConveter (see {@link
 * ConverterRegistry TypeConveterManager} for more information
 * about retrieval). Next, the Converter can be used
 * for conversions using one of the methods described
 * below. Any given Converter may be used to convert to
 * many different types because of the way that the
 * ConverterRegistry searches for TypeConverters. Because
 * of this flexibility when converting, the Converter
 * must be told what type to convert to. This is the reason
 * for the second convertTo parameter on all of the convert
 * methods.
 * </p>
 *
 * <p>
 * The type that appears on all of the methods in this interface can
 * be any type that is support by the Converter, but generally is
 * a type that is the type or a sub-type of the type the converter is
 * registered for. For example, let's say the converter is registered
 * to java.lang.Number, then this converter should be able to convert
 * Integer, Long, Double, etc. unless a converter is registered for
 * each one of these. So, we could call the converter with ("0.5",
 * Double.class) in order to convert the String to a Double object.
 * </p>
 *
 * <h3>Arrays</h3>
 * <p>
 * Arrays pose a significant issue when considering null values and
 * multi-dimensional arrays as well as communicative conversion (i.e.
 * a -> b -> a). Because of this, implementations are free to NOT
 * support multi-dimensional arrays, null values or communicative
 * conversions. When supporting these concepts however implementations
 * must take care to use array delimiters and null indicators. The
 * recommended versions of these are:
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Recommendation</th></tr>
 * <tr><td>Array open</td><td>{</td></tr>
 * <tr><td>Array close</td><td>}</td></tr>
 * <tr><td>Array separator</td><td>,</td></tr>
 * <tr><td>Null indicator</td><td>__#null#__</td></tr>
 * </table>
 *
 * <p>
 * These defaults are stored as variables in this interface as well.
 * </p>
 *
 * <h3>Null values</h3>
 * <p>
 * When peforming any conversion of a single value that is null, it
 * is recommended that implementations return null. This however doesn't
 * work for values inside arrays as previously mentioned. In that case
 * a String should be used that is sufficiently complex enough to ensure
 * that it will not show up in the majority of Strings being converted
 * unless a converter put it there.
 * </p>
 *
 * <h3>Primitives</h3>
 * <p>
 * There are already pre-written conversion for primitives, but when
 * overriding these, implementations must ensure that during conversions
 * of nulls, that the default value of the primitive is returned.
 * </p>
 *
 * @author  Brian Pontarelli
 * @since   1.0
 */
public interface Converter {

    /**
     * The default string that denotes an array start.
     */
    String DEFAULT_ARRAY_START = "{";

    /**
     * The default string that denotes an array end.
     */
    String DEFAULT_ARRAY_END = "}";

    /**
     * The default string that denotes different values in an array.
     */
    String DEFAULT_ARRAY_SEPARATOR = ",";

    /**
     * The default String that represents null.
     */
    String NULL_INDICATOR = "__#null#__";

    /**
     * <p>
     * Converts the given object to the given type.
     * </p>
     *
     * <p>
     * It is common practice to call the convertString method from this method passing it value.toString
     * and convertTo. Of course this is not required and is up to the implementing class to determine
     * how best to convert the value to the given type.
     * </p>
     *
     * <p>
     * Another common practice is to handle the case where value is an instance of the type the
     * converter is registered under and the convertTo is actually a String. This implies that the
     * conversion is a reverse conversion from the type to a String. In this case it is common to call
     * the {@link #convertToString(Object,Class, java.util.Locale , java.util.Map)} method but passing in the type of the
     * value (i.e. the type the converter is registered for) as the convertFrom parameter of that
     * method. This isn't necessary but a good idea. Otherwise it can be left null if the value is
     * not null.
     * </p>
     *
     * <p>
     * It is also common practice to handle array types in this method by checking if the convertTo
     * type is an array and then calling {@link #convertToArray(Object, Class, java.util.Locale , java.util.Map)} correctly.
     * This reduces the amount of work necessary for clients to convert values. See the
     * {@link Class#isArray()} method for more information.
     * </p>
     *
     * <p>
     * Lastly, it is common practice in this method to handle the case where the convertTo is not an
     * array and the value is an array. In this case it is common to call the
     * {@link #convertArray(Object, Class, java.util.Locale , java.util.Map)} method in order to handle converting the
     * value array to an object.
     * </p>
     *
     * @param   value The value to convert.
     * @param   convertTo The type to convert the value to.
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The converted value.
     * @throws  ConversionException If there was a problem converting the given value to the
     *          given type.
     */
    <T> T convert(Object value, Class<T> convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * <p>
     * Converts the given String to the given type.
     * </p>
     *
     * <p>
     * It is also common practice to handle array types in this method by checking if the convertTo
     * type is an array and then calling
     * {@link #convertStringToArray(String, String, String, String, Class, Locale, Map)}  correctly.
     * This reduces the amount of work necessary for clients to convert values. See the
     * {@link Class#isArray()} method for more information.
     * </p>
     *
     * <p>
     * The {@link #convertStringToArray(String, String, String, String, Class, Locale, Map)} and this
     * method can handle arrays with any number of dimensions, but this is not required because of the
     * complexity of handling nested arrays. If a sub-class doesn't handle nested-arrays, it should
     * say so explicitly and throw appropriate, well documented exceptions with good error messages.
     * Likewise, the ability to handle null values inside arrays or null nested arrays is complex. This
     * handling is also not required and should be clearly explained and exceptions should be thrown.
     * Lastly, it should be expected that Objects produced from this method can be converted back to
     * an identical String (sans whitespace) using the
     * {@link #convertArrayToString(Object, String, String, String, Class, Locale, Map)} method.
     * </p>
     *
     * @param   value The String value to convert
     * @param   convertTo The type to convert the value to
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The converted value.
     * @throws  ConversionException If there was a problem converting the
     *          given value to the given type
     */
    <T> T convertString(String value, Class<T> convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * <p>
     * Converts the given Object to a String.
     * </p>
     *
     * <p>
     * It is also common practice to handle array types in this method by checking if the value or
     * the convertFrom parameter is an array and then calling
     * {@link #convertArrayToString(Object, String, String, String, Class, Locale, Map)} correctly.
     * This reduces the amount of work necessary for sub-classes to convert values. See the
     * {@link Class#isArray()} method for more information.
     * </p>
     *
     * @param   value The value to convert to a String.
     * @param   convertFrom The type of the value in case value is null.
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The String.
     * @throws  ConversionException If there was a problem converting the given value to a String.
     */
    <T> String convertToString(T value, Class<T> convertFrom, Locale locale, Map parameters) throws ConversionException;

    /**
     * <p>
     * Converts the given array to an object. This method is mostly used to take an array of length 1
     * and convert the 0th indices object to the given type. The type can be any type, but normally
     * is the type or a sub-type of the type the converter is registered for.
     * </p>
     *
     * <p>
     * It is also common practice to handle array types in this method by checking if the convertTo
     * type is an array and then calling the convertArrayToArray method.
     * </p>
     *
     * @param   array The array to convert to an object
     * @param   convertTo The type to convert the array to
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The converted value
     * @throws  ConversionException If there was a problem converting the given
     *          array to an Obejct
     */
    <T> T convertArray(Object array, Class<T> convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * <p>
     * Converts the array to an array of the given type. The given type can be either an array or
     * object, for this method to succeed. If the given type is an array this method should retrieve
     * the component type of the array and construct a new array of the component type. It could then
     * convert each array position in the value array to the given type (or the component type if
     * convertTo is an array) and store them in the new array.
     * </p>
     *
     * @param   array The array to convert to an array.
     * @param   convertTo Either the array type of the component type for the array being converted to.
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The converted array.
     * @throws  ConversionException If there was a problem converting the given array to an array.
     */
    <T> T convertArrayToArray(Object array, Class<T> convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * <p>
     * Converts the given object to an array of objects of the given type.
     * </p>
     *
     * <p>
     * This method should also take care to check if the convertTo type is an array. If it is, it is
     * should retrieve the component type and use that when constructing the return array and also
     * when converting the value. Of course, it is up to the implementing method to determine how best
     * to convert the value to an array.
     * </p>
     *
     * @param   value The value to convert to an array.
     * @param   convertTo The type of the array being converted to.
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The converted array.
     * @throws  ConversionException If there was a problem converting the given value to the
     *          given type.
     */
    <T> T convertToArray(Object value, Class<T> convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * <p>
     * Converts the String to an array of objects.
     * </p>
     *
     * <p>
     * Usually this method will use a StringTokenizer or some similar method to break up the String
     * into separate values and then convert each one and store the result in an array. The delimiter
     * and start/end Strings are given to help in the parsing of the String. The start and end Strings
     * are used to delimit the beginning and end of an array within the String and are useful for
     * multi-dimensional arrays. They are optional and for a single dimensional array these delimiters
     * need not exist in the String. The delimiter is optional but if not supplied a default value is
     * used. This delimiter is used to break up the String so that each part of the String can be
     * converted and stored into the new result array.
     * </p>
     *
     * @param   value The String value to convert to an array.
     * @param   delimiter (Optional) The character that separates array values. This character is used
     *          to split the String value of the array (in conjunction with the start and end characters)
     *          and those values are then trimmed.
     * @param   arrayStartSeparator (Optional) The String used at the start of the array String.
     * @param   arrayEndSeparator (Optional) The String used at the end of the array String.
     * @param   convertTo The type of the array being converted to.
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The converted array.
     * @throws  ConversionException If there was a problem converting the given value to the
     *          given type.
     */
    <T> T convertStringToArray(String value, String delimiter, String arrayStartSeparator,
            String arrayEndSeparator, Class<T> convertTo, Locale locale, Map parameters)
    throws ConversionException;

    /**
     * <p>
     * Converts the array of objects to a String.
     * </p>
     *
     * <p>
     * This should use the delimiter to build the String and the start and end Strings to separate
     * out arrays in a multi-dimensional array. The start and end separators are not required, so for
     * single-dimensional arrays, they need not be used to build the String. If the delimiter is not
     * given, than a default of comma character is used.
     * </p>
     *
     * @param   value The array value to convert to a String.
     * @param   delimiter (Optional) The character used to separate array values.
     * @param   arrayStartSeparator (Optional) The character used at the start of the array String.
     * @param   arrayEndSeparator (Optional) The character used at the end of the array String.
     * @param   convertFrom (Optional) The type of the array being converted to in case value is null.
     * @param   locale (Optional) The locale used during conversion to support i18n type conversions
     *          (such as dates). If this is null the Converter implementation will assume the system
     *          default Locale if it needs a Locale to convert.
     * @param   parameters (Optional) A Map of extra parameters that can be used by the converter to
     *          help in the conversion of the value. For example, the Date type converter might need
     *          the date format to use when parsing Strings.
     * @return  The array converted into a String.
     * @throws  ConversionException If there was a problem converting the given value to a String.
     */
    <T> String convertArrayToString(T value, String delimiter, String arrayStartSeparator,
            String arrayEndSeparator, Class<T> convertFrom, Locale locale, Map parameters)
    throws ConversionException;
}