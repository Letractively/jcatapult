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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.opensymphony.xwork2.XWorkException;

/**
 * <p>
 * This class converts the value from a form or parameter into a Joda
 * DateTime object. This just brute force attempts as many formats as
 * it can if there is no format supplied. The way to supply a format
 * for the DateTime is to specify a parameter attribute that will be
 * processed by the {@link org.jcatapult.struts.interceptor.JCatapultParametersInterceptor}.
 * The name of the attribute is <strong>dateTimeFormat</strong>.
 * This is usually handled via a hidden input field that uses the
 * attribute notation like this:
 * </p>
 *
 * <pre>
 * &lt;s:textfield name="date"/>
 * &lt;s:hidden name="date@dateTimeFormat" value="MM-dd-yyyy"/>
 * </pre>
 *
 * @author  Brian Pontarelli
 */
public class DateTimeTypeConverter extends BaseTypeConverter {
    public static final String[] PATTERNS = {
        "MM/dd/yy", "MM-dd-yy",
        "MM/dd/yy hh:mm aa", "MM-dd-yy hh:mm aa",
        "MM/dd/yy hh:mm:ss aa", "MM-dd-yy hh:mm:ss aa",
        "MM/dd/yy hh:mm:ss aa zz", "MM-dd-yy hh:mm:ss aa zz"};
    private static final Logger logger = Logger.getLogger(DateTimeTypeConverter.class.getName());

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        String fullPropertyName = (String) context.get("current.property.path");
        if (toType == String.class) {
            return convertToString(context, fullPropertyName, value);
        } else if (value instanceof String[]) {
            return convertFromString(context, member, fullPropertyName, (String[]) value);
        } else if (value instanceof String) {
            return convertFromString(context, member, fullPropertyName, new String[]{(String) value});
        } else {
            throw new IllegalArgumentException("Unable to convert to [" + toType + "] using the " +
                this.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    public Object convertFromString(Map context, Member member, String propertyName, String[] values) {
        String format = (String) getAttributes(context).get("dateTimeFormat");
        DateTime dateTime = null;
        if (format == null) {
            // Brute force test all the patterns
            for (String pattern : PATTERNS) {
                try {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
                    dateTime = dateTimeFormatter.parseDateTime(values[0]);
                    context.put(makeKey(propertyName), pattern);
                    break;
                } catch (Exception e) {
                }
            }
        } else {
            DateTimeFormatter dateTimeFormatter;
            try {
                dateTimeFormatter = DateTimeFormat.forPattern(format);
            } catch (Exception e) {
                RuntimeException re = new XWorkException("Attempting to convert a String to a " +
                    "DateTime on [" + member.getDeclaringClass() + "#" + member.getName() +
                    "] with a propertyName of [" + propertyName + "] but the format was [" + format +
                    "] and that is an invalid format for the Joda DateTimeFormatter.");
                logger.log(Level.FINE, re.getMessage(), re);
                throw re;
            }

            try {
                dateTime = dateTimeFormatter.parseDateTime(values[0]);
            } catch (Exception e) {
                RuntimeException re = new XWorkException("Attempting to convert a String to a " +
                    "DateTime on [" + member.getDeclaringClass() + "#" + member.getName() +
                    "] with a propertyName of [" + propertyName + "] but the format was [" + format +
                    "] and the value [" + values[0] + "] couldn't be parsed.");
                logger.log(Level.FINE, re.getMessage(), re);
                throw re;
            }
        }

        if (dateTime == null) {
            RuntimeException re = new XWorkException("Attempting to convert a String to a " +
                "DateTime on [" + member.getDeclaringClass() + "#" + member.getName() +
                "] with a propertyName of [" + propertyName + "] but the value [" + values[0] +
                "] couldn't be parsed.");
            logger.log(Level.FINE, re.getMessage(), re);
            throw re;
        }

        return dateTime;
    }

    public String convertToString(Map context, String propertyName, Object value) {
        String format = (String) context.get(makeKey(propertyName));
        if (format == null) {
            RuntimeException re = new IllegalArgumentException("Attempting to convert a DateTime to a " +
                "String from the propertyName [" + propertyName + "] but the format wasn't no longer " +
                "in the OGNL context.");
            logger.log(Level.SEVERE, re.getMessage(), re);
            throw re;
        }

        String result;
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format.toString());
            DateTime dateTime = (DateTime) value;
            result = dateTimeFormatter.print(dateTime);
        } catch (Exception e) {
            RuntimeException re = new IllegalArgumentException("Attempting to convert a DateTime to a " +
                "String from the propertyName [" + propertyName + "] but the date format failed.", e);
            logger.log(Level.SEVERE, re.getMessage(), re);
            throw re;
        }

        return result;
    }

    protected String makeKey(String propertyName) {
        return propertyName + "@dateTimeFormat";
    }
}