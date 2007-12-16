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
package org.jcatapult.struts.convert;

import java.lang.reflect.Member;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ognl.TypeConverter;

/**
 * <p>
 * This class converts the value from a form or parameter into any JDK 1.5
 * enumeration class instance. If the value given isn't a valid enumeration
 * value, this throws an exception.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class EnumTypeConverter implements TypeConverter {
    private static final Logger logger = Logger.getLogger(EnumTypeConverter.class.getName());

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (toType.equals(String.class)) {
            return value.toString();
        }

        if (!Enum.class.isAssignableFrom(toType)) {
            RuntimeException re = new IllegalArgumentException("The EnumTypeConverter cannot convert instances of [" +
                toType + "]");
            logger.log(Level.SEVERE, re.getMessage(), re);
            throw re;
        }

        String enumValue;
        if (value instanceof String[]) {
            enumValue = ((String[]) value)[0];
        } else if (value instanceof String) {
            enumValue = (String) value;
        } else {
            RuntimeException re = new IllegalArgumentException("Unable to convert to [" + toType +
                "] using the " + this.getClass());
            logger.log(Level.SEVERE, re.getMessage(), re);
            throw re;
        }

        return Enum.valueOf(toType, enumValue);
    }
}