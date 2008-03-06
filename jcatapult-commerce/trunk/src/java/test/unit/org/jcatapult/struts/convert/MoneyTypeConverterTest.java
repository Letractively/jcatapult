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

import java.lang.reflect.Method;
import java.util.Currency;
import java.util.Map;
import javax.servlet.ServletRequest;

import org.easymock.EasyMock;
import org.jcatapult.domain.commerce.Money;
import org.jcatapult.servlet.ServletObjectsHolder;
import static org.junit.Assert.*;
import org.junit.Test;

import com.opensymphony.xwork2.XWorkException;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This class tests the money type converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MoneyTypeConverterTest {
    @Test
    public void testToMoney() {
        ServletRequest request = EasyMock.createNiceMock(ServletRequest.class);
        EasyMock.expect(request.getAttribute("foo.bar#attributes")).andReturn(mapNV("currencyCode", "USD"));
        EasyMock.replay(request);
        ServletObjectsHolder.setServletRequest(request);

        String[] values = {"1.99"};
        Map context = mapNV("conversion.property.fullName", "foo.bar");
        MoneyTypeConverter converter = new MoneyTypeConverter();
        Money money = (Money) converter.convertValue(context, null, null, null, values, Money.class);
        assertNotNull(money);
        assertEquals("1.99", money.toNumericString());
        assertEquals(Currency.getInstance("USD"), money.getCurrency());
    }

    @Test
    public void testToMoneyFailureSingleParameter() throws NoSuchMethodException {
        ServletRequest request = EasyMock.createNiceMock(ServletRequest.class);
        EasyMock.replay(request);
        ServletObjectsHolder.setServletRequest(request);

        String[] values = {"1.5"};
        MoneyTypeConverter converter = new MoneyTypeConverter();
        try {
            Method method = getClass().getMethod("testToMoney");
            Map context = mapNV("conversion.property.fullName", "foo.bar");
            converter.convertValue(context, null, method, null, values, Money.class);
            fail("Should have failed");
        } catch (XWorkException e) {
            // Expected.
        }
    }

    @Test
    public void testToMoneyFailureNoLength3() throws NoSuchMethodException {
        ServletRequest request = EasyMock.createNiceMock(ServletRequest.class);
        EasyMock.expect(request.getAttribute("foo.bar#attributes")).andReturn(mapNV("currencyCode", "US"));
        EasyMock.replay(request);
        ServletObjectsHolder.setServletRequest(request);

        String values = "1.99";
        MoneyTypeConverter converter = new MoneyTypeConverter();
        try {
            Method method = getClass().getMethod("testToMoney");
            Map context = mapNV("conversion.property.fullName", "foo.bar");
            converter.convertValue(context, null, method, null, values, Money.class);
            fail("Should have failed");
        } catch (XWorkException e) {
            // Expected.
        }
    }

    @Test
    public void testToMoneyFailureBadCurrencyCode() throws NoSuchMethodException {
        ServletRequest request = EasyMock.createNiceMock(ServletRequest.class);
        EasyMock.expect(request.getAttribute("foo.bar#attributes")).andReturn(mapNV("currencyCode", "BAD"));
        EasyMock.replay(request);
        ServletObjectsHolder.setServletRequest(request);

        String values = "1.99";
        MoneyTypeConverter converter = new MoneyTypeConverter();
        try {
            Method method = getClass().getMethod("testToMoney");
            Map context = mapNV("conversion.property.fullName", "foo.bar");
            converter.convertValue(context, null, method, null, values, Money.class);
            fail("Should have failed");
        } catch (XWorkException e) {
            // Expected.
        }
    }
}