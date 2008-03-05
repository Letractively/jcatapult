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
import java.util.Currency;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcatapult.domain.commerce.Money;

import com.opensymphony.xwork2.XWorkException;
import ognl.TypeConverter;

/**
 * <p>
 * This class converts the value from a form or parameter into a Money
 * object. The currency code must be provided and there is only one way
 * to provide this to the type covnerter. You must have two parameters
 * of the same name set into the HTTP request. One is the amount and the
 * other is the currency code.Here's an example Struts form:
 * </p>
 *
 * <pre>
 * &lt;s:textfield name="money"/>
 * &lt;s:hidden name="money" value="USD"/>
 * </pre>
 *
 * <p>
 * If no currency code is set, this will throw an exception.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MoneyTypeConverter implements TypeConverter {
    private static final Logger logger = Logger.getLogger(MoneyTypeConverter.class.getName());

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        String fullPropertyName = (String) context.get("current.property.path");
        String currencyCode = (String) context.get(fullPropertyName + "@currencyCode");
        if (currencyCode == null) {
            XWorkException exception  = new XWorkException("Attempting to convert a String to a " +
                "Money on [" + member.getDeclaringClass() + "#" + member.getName() +
                "] with a propertyName of [" + fullPropertyName + "] but the currency code was not setup " +
                "using the JCatapult parameter attributes. Consult the JCatapultParametersInterceptor " +
                "JavaDoc to determine how to use parameter attributes and then pass in a parameter " +
                "attribute named currencyCode.");
            logger.log(Level.SEVERE, exception.getMessage(), exception);
            throw exception;
        }

        if (toType.equals(String.class)) {
            return convertToString(value);
        } else if (value instanceof String[]) {
            return convertFromString(member, fullPropertyName, ((String[]) value)[0], currencyCode);
        } else if (value instanceof String) {
            return convertFromString(member, fullPropertyName, (String) value, currencyCode);
        } else {
            throw new IllegalArgumentException("Unable to convert to [" + toType + "] using the " +
                this.getClass());
        }
    }

    public Object convertFromString(Member member, String propertyName, String value, String currencyCode) {
        Currency currency;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (Exception e) {
            XWorkException exception  = new XWorkException("Attempting to convert a String to a " +
                "Money on [" + member.getDeclaringClass() + "#" + member.getName() +
                "] with a propertyName of [" + propertyName + "] but the currency code [" +
                currencyCode + "] is invalid.");
            logger.log(Level.SEVERE, exception.getMessage(), exception);
            throw exception;
        }

        return Money.valueOf(value, currency);
    }

    public String convertToString(Object value) {
        Money money = (Money) value;
        return money.toNumericString();
    }
}