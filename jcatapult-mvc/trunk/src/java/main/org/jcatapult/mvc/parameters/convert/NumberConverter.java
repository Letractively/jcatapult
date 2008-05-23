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
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class NumberConverter extends AbstractConverter {
    /**
     * Returns 0 for everything but in the correct wrapper classes.
     */
    protected Object defaultPrimitive(Class convertTo, Locale locale, Map parameters) {
        if (convertTo == Byte.TYPE) {
            return new Byte((byte) 0);
        } else if (convertTo == Short.TYPE) {
            return new Short((short) 0);
        } else if (convertTo == Integer.TYPE) {
            return new Integer(0);
        } else if (convertTo == Long.TYPE) {
            return new Long(0l);
        } else if (convertTo == Float.TYPE) {
            return new Float(0.0f);
        } else if (BigInteger.class.equals(convertTo)) {
            return BigInteger.ZERO;
        } else if (BigDecimal.class.equals(convertTo)) {
            return new BigDecimal(new Double(0.0).doubleValue());
        }

        return new Double(0.0);
    }

    /**
     * Uses the valueOf methods in the wrapper classes based on the convertTo type.
     */
    protected Object stringToPrimitive(String value, Class convertTo, Locale locale, Map parameters) {
        try {
            if (convertTo == Byte.TYPE) {
                return Byte.valueOf(value);
            } else if (convertTo == Short.TYPE) {
                return Short.valueOf(value);
            } else if (convertTo == Integer.TYPE) {
                return Integer.valueOf(value);
            } else if (convertTo == Long.TYPE) {
                return Long.valueOf(value);
            } else if (convertTo == Float.TYPE) {
                return Float.valueOf(value);
            }

            return handleBigTypes(convertTo, value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

    /**
     * Uses the valueOf methods in the wrapper classes based on the convertTo type.
     */
    protected Object stringToObject(String value, Class convertTo, Locale locale, Map parameters)
    throws ConversionException {
        try {
            if (convertTo == Byte.class) {
                return Byte.valueOf(value);
            } else if (convertTo == Short.class) {
                return Short.valueOf(value);
            } else if (convertTo == Integer.class) {
                return Integer.valueOf(value);
            } else if (convertTo == Long.class) {
                return Long.valueOf(value);
            } else if (convertTo == Float.class) {
                return Float.valueOf(value);
            }

            return handleBigTypes(convertTo, value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

    private Object handleBigTypes(Class convertTo, String value) {

        if(convertTo == null) {
            return Double.valueOf(value);
        }
        try {
            if(convertTo.equals(BigInteger.class)) {
                return new BigInteger(value);
            } else if(convertTo.equals(BigDecimal.class)) {
                return new BigDecimal(value);
            }
            return Double.valueOf(value);
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    /**
     * Supports all Number types and primitive types except Boolean and Character.
     */
    protected boolean supportsConvertToType(Class type) {
        return (Number.class.isAssignableFrom(type) ||
            (type.isPrimitive() && type != Boolean.TYPE && type != Character.TYPE));
    }
}