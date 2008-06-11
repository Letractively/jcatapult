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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.ConverterStateException;

import com.google.inject.Singleton;
import static net.java.util.CollectionTools.array;

/**
 * <p>
 * This is the type converter for primitives and wrapper classes of
 * numbers.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
@Singleton
public class NumberConverter extends AbstractPrimitiveConverter {
    /**
     * Returns 0 for everything but in the correct wrapper classes.
     */
    protected <T> T defaultPrimitive(Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        if (convertTo == Byte.TYPE || convertTo == Byte.class) {
            return (T) new Byte((byte) 0);
        } else if (convertTo == Short.TYPE || convertTo == Short.class) {
            return (T) new Short((short) 0);
        } else if (convertTo == Integer.TYPE || convertTo == Integer.class) {
            return (T) new Integer(0);
        } else if (convertTo == Long.TYPE || convertTo == Long.class) {
            return (T) new Long(0l);
        } else if (convertTo == Float.TYPE || convertTo == Float.class) {
            return (T) new Float(0.0f);
        } else if (convertTo == BigInteger.class) {
            return (T) BigInteger.ZERO;
        } else if (convertTo == BigDecimal.class) {
            return (T) new BigDecimal(new Double(0.0).doubleValue());
        } else if (convertTo == Double.TYPE || convertTo == Double.class) {
            return (T) new Double(0.0);
        }

        throw new ConverterStateException("Invalid type for NumberConverter [" + convertTo + "]");
    }

    /**
     * Uses the valueOf methods in the wrapper classes based on the convertTo type.
     */
    protected <T> T stringToPrimitive(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        try {
            if (convertTo == Byte.TYPE || convertTo == Byte.class) {
                return (T) Byte.valueOf(value);
            } else if (convertTo == Short.TYPE || convertTo == Short.class) {
                return (T) Short.valueOf(value);
            } else if (convertTo == Integer.TYPE || convertTo == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (convertTo == Long.TYPE || convertTo == Long.class) {
                return (T) Long.valueOf(value);
            } else if (convertTo == Float.TYPE || convertTo == Float.class) {
                return (T) Float.valueOf(value);
            } else if (convertTo == Double.TYPE || convertTo == Double.class) {
                return (T) Double.valueOf(value);
            } else if (convertTo == BigInteger.class) {
                return (T) new BigInteger(value);
            } else if (convertTo == BigDecimal.class) {
                return (T) new BigDecimal(value);
            }

            throw new ConverterStateException("Invalid type for NumberConverter [" + convertTo + "]");
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

    protected <T> String primitiveToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return value.toString();

        // TODO add precision support
//        if (convertFrom == Byte.TYPE || convertFrom == Byte.class ||
//                convertFrom == Short.TYPE || convertFrom == Short.class ||
//                convertFrom == Integer.TYPE || convertFrom == Integer.class ||
//                convertFrom == Long.TYPE || convertFrom == Long.class) {
//            return value.toString();
//        } else if (convertFrom == Float.TYPE || convertFrom == Float.class ||
//                convertFrom == Double.TYPE || convertFrom == Double.class) {
//            return (T) Double.valueOf(value);
//        } else if (convertFrom == BigInteger.class) {
//            return (T) new BigInteger(value);
//        } else if (convertFrom == BigDecimal.class) {
//            return (T) new BigDecimal(value);
//        }
    }

    /**
     * Returns all the number primitives, Number.class, BigInteger.class and BigDecimal.class.
     */
    public Class<?>[] supportedTypes() {
        return array(Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE,
            Number.class, BigDecimal.class, BigInteger.class);
    }
}