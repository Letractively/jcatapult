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
package org.jcatapult.mvc.parameters.convert.converters;

import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameters.convert.ConverterStateException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import static net.java.lang.ObjectTools.*;
import net.java.lang.StringTools;
import static net.java.util.CollectionTools.array;

/**
 * <p>
 * This converts to and from Strings.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
@Singleton
public class StringConverter extends AbstractConverter {
    private boolean emptyIsNull = true;

    @Inject(optional = true)
    public void setEmptyStringIsNull(@Named("jcatapult.mvc.emptyStringIsNull") boolean emptyIsNull) {
        this.emptyIsNull = emptyIsNull;
    }

    protected <T> T stringToObject(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws org.jcatapult.mvc.parameters.convert.ConversionException, ConverterStateException {
        if (emptyIsNull && StringTools.isTrimmedEmpty(value)) {
            return null;
        }

        return (T) value;
    }

    protected <T> T stringsToObject(String[] values, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws org.jcatapult.mvc.parameters.convert.ConversionException, ConverterStateException {
        return (T) join(values, ",");
    }

    protected <T> String objectToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws org.jcatapult.mvc.parameters.convert.ConversionException, ConverterStateException {
        return value.toString();
    }

    /**
     * Returns String.class.
     */
    public Class<?>[] supportedTypes() {
        return array(String.class);
    }
}