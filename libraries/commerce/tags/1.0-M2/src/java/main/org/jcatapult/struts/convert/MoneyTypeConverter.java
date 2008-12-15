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
import net.java.lang.StringTools;

/**
 * <p>
 * This class converts the value from a form or parameter into a Money
 * object. The currency code must be provided and there is only one way
 * to provide this to the type covnerter. You must specify an additional
 * attribute on the form field tag inside the JSP. This attribute must be
 * named <strong>currencyCode</strong>. Here's an example Struts textfield
 * tag with the additional attribute:
 * </p>
 *
 * <pre>
 * &lt;s:textfield name="money" currencyCode="USD"/>
 * </pre>
 *
 * <p>
 * If no currency code is set, this will throw an exception.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MoneyTypeConverter extends BaseTypeConverter {
    private static final Logger logger = Logger.getLogger(MoneyTypeConverter.class.getName());

    public Object convertValue(Map context, Object target, Member member, String propertyName,
            Object value, Class toType) {
        String fullPropertyName = getPropertyName(context);
        String currencyCode = (String) getAttributes(context).get("currencyCode");
        if (currencyCode == null) {
            XWorkException exception  = new XWorkException("Attempting to convert a String to a " +
                "Money on [" + member.getDeclaringClass() + "#" + member.getName() +
                "] with a propertyName of [" + fullPropertyName + "] but the currency code was not setup " +
                "on the form field tag. Consult the JavaDoc for the MoneyTypeConverter class in the " +
                "jcatapult-commerce library for more information.");
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
        // Handle the null case
        if (StringTools.isTrimmedEmpty(value)) {
            return null;
        }

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
        // Handle the null case
        if (value == null) {
            return null;
        }
        
        Money money = (Money) value;
        return money.toNumericString();
    }
}