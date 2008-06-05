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
import net.java.lang.StringTools;
import static net.java.util.CollectionTools.array;

/**
 * <p>
 * This class is the type covnerter for booleans.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
@Singleton
public class BooleanConverter extends AbstractPrimitiveConverter {
    /**
     * Returns false.
     */
    protected <T> T defaultPrimitive(Class<T> convertTo, HttpServletRequest request,
        HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return (T) Boolean.FALSE;
    }

    /**
     * Uses Boolean.valueOf. Throws an exception if the String is not a valid boolean as dictated by
     * the {@link StringTools#isValidBoolean(String)} method.
     */
    protected <T> T stringToPrimitive(String value, Class<T> convertTo, HttpServletRequest request,
        HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        if (!StringTools.isValidBoolean(value)) {
            throw new ConversionException ("Unable to convert invalid boolean String [" + value + "]");
        }

        return (T) Boolean.valueOf(value);
    }

    /**
     * Returns value.toString().
     */
    protected <T> String primitiveToString(T value, Class<T> convertFrom, HttpServletRequest request,
        HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return value.toString();
    }

    /**
     * Returns Boolean.Type and Boolean.class.
     */
    public Class<?>[] supportedTypes() {
        return array(Boolean.TYPE, Boolean.class);
    }
}