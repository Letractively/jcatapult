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

import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.ConverterStateException;

import com.google.inject.Singleton;
import static net.java.util.CollectionTools.array;

/**
 * <p>
 * This class is the type converter for characters.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
@Singleton
public class CharacterConverter extends AbstractPrimitiveConverter {
    /**
     * Returns a single character with a unicode value of 0.
     */
    protected <T> T defaultPrimitive(Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return (T) new Character('\u0000');
    }

    /**
     * If String is longer than one character, this throws an exception. Otherwise, that character is
     * returned. If the value is null or empty, this throws an exception.
     */
    protected <T> T stringToPrimitive(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        if (value.length() > 1) {
            throw new ConversionException("Conversion from String to character must be a String" +
                " of length 1 - [" + value + "] is invalid");
        }

        return (T) new Character(value.charAt(0));
    }

    protected <T> String primitiveToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return value.toString();
    }

    /**
     * Returns Character.TYPE and Character.class.
     */
    public Class<?>[] supportedTypes() {
        return array(Character.TYPE, Character.class);
    }
}