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

import java.util.Map;

import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.ConverterStateException;
import org.jcatapult.mvc.parameter.convert.annotation.Converter;

import net.java.lang.StringTools;

/**
 * <p>
 * This class is the type covnerter for booleans.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Converter(forTypes = {Boolean.class, boolean.class})
@SuppressWarnings("unchecked")
public class BooleanConverter extends AbstractPrimitiveConverter {
    /**
     * Returns false.
     */
    protected <T> T defaultPrimitive(Class<T> convertTo, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return (T) Boolean.FALSE;
    }

    /**
     * Uses Boolean.valueOf. Throws an exception if the String is not a valid boolean as dictated by
     * the {@link StringTools#isValidBoolean(String)} method.
     */
    protected <T> T stringToPrimitive(String value, Class<T> convertTo, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        if (!StringTools.isValidBoolean(value)) {
            throw new ConversionException ("Unable to convert invalid boolean String [" + value + "]");
        }

        return (T) Boolean.valueOf(value);
    }

    /**
     * Returns value.toString().
     */
    protected <T> String primitiveToString(T value, Class<T> convertFrom, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        return value.toString();
    }
}