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

import java.io.File;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.ConverterStateException;

import com.google.inject.Singleton;
import static net.java.lang.ObjectTools.*;
import net.java.lang.StringTools;
import static net.java.util.CollectionTools.array;

/**
 * <p>
 * This class converts to and from the java.io.File class.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
@Singleton
public class FileConverter extends AbstractConverter {
    /**
     * Returns null if the value is null, otherwise this returns a new File of the value.
     *
     * @param   attributes Can contain the parentDir attribute which if the String is relative will
     *          be the parent directory.
     */
    protected <T> T stringToObject(String value, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        if (StringTools.isTrimmedEmpty(value)) {
            return null;
        }

        if (value.charAt(0) == File.separatorChar || value.charAt(0) == '\\') {
            return (T) new File(value);
        }

        if (attributes != null) {
            String parent = attributes.get("parentDir");
            if (parent != null) {
                return (T) new File(parent, value);
            }
        }

        return (T) new File(value);
    }

    /**
     * Joins the values and then sends the new joined String to the stringToObject method.
     */
    protected <T> T stringsToObject(String[] values, Class<T> convertTo, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        String joined = join(values, File.separator);
        return stringToObject(joined, convertTo, request, response, locale, attributes);
    }

    /**
     * Returns the absolute path of the file.
     */
    protected <T> String objectToString(T value, Class<T> convertFrom, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException {
        File file = (File) value;
        return file.getAbsolutePath();
    }

    /**
     * Returns File.class.
     */
    public Class<?>[] supportedTypes() {
        return array(File.class);
    }
}