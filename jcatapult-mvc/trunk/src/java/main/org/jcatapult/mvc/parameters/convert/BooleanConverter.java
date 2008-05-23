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

import net.java.lang.StringTools;

/**
 * <p>
 * This class is the type covnerter for booleans.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class BooleanConverter extends AbstractPrimitiveConverter {
    /**
     * Returns false.
     */
    protected Object defaultPrimitive(Class convertTo, Locale locale, Map parameters) {
        return Boolean.FALSE;
    }

    /**
     * Uses Boolean.valueOf. Throws an exception if the String is not a valid boolean as dictated by
     * the {@link StringTools#isValidBoolean(String)} method.
     */
    protected Object stringToPrimitive(String value, Class convertTo, Locale locale, Map parameters) {
        if (!StringTools.isValidBoolean(value)) {
            throw new ConversionException ("Unable to convert invalid boolean String [" + value + "]");
        }

        return Boolean.valueOf(value);
    }

    /**
     * Calls {@link #stringToPrimitive(String, Class, Locale, Map)}.
     */
    protected Object stringToObject(String value, Class convertTo, Locale locale, Map parameters) {
        return stringToPrimitive(value, convertTo, locale, parameters);
    }

    /**
     * Returns true if the type is the primitive or wrapper Boolean.
     */
    protected boolean supportsConvertToType(Class type) {
        return (type == Boolean.TYPE || type == Boolean.class);
    }
}