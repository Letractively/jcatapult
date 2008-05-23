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
 * This class is the type converter for characters.
 * </p>
 *
 * @author  Brian Pontarelli
 * @since   1.0
 */
public class CharacterConverter extends AbstractPrimitiveConverter {
    /**
     * Returns the 0 character in unicode.
     */
    protected Object defaultPrimitive(Class convertTo, Locale locale, Map parameters) {
        return new Character('\u0000');
    }

    /**
     * If String is longer than one character, this throws an exception. Otherwise, that character is
     * returned. If the value is null or empty, this throws an exception.
     */
    protected Object stringToPrimitive(String value, Class convertTo, Locale locale, Map parameters) {
        if (value.length() > 1) {
            throw new ConversionException("Conversion from String to character must be a String" +
                " of length 1 - [" + value + "] is invalid");
        }

        return new Character(value.charAt(0));
    }

    /**
     * Calls {@link #stringToPrimitive(String, Class, Locale, Map)}.
     */
    protected Object stringToObject(String value, Class convertTo, Locale locale, Map parameters) {
        return stringToPrimitive(value, convertTo, locale, parameters);
    }

    /**
     * Returns true if the type is the primitive or wrapper Character.
     */
    protected boolean supportsConvertToType(Class type) {
        return (type == Character.TYPE || type == Character.class);
    }
}