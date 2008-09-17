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

import java.util.Collections;
import java.util.Locale;

import org.jcatapult.domain.commerce.Money;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.ConverterStateException;
import org.jcatapult.mvc.parameter.convert.GlobalConverter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the Money converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MoneyConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        GlobalConverter converter = new MoneyConverter();
        Money value = (Money) converter.convertFromStrings(array((String) null), Money.class, null, "testExpr");
        assertNull(value);

        value = (Money) converter.convertFromStrings(array("7.00"), Locale.class, map("currencyCode", "USD"), "testExpr");
        assertTrue(Money.valueOfUSD("7.00").equalsExact(value));

        value = (Money) converter.convertFromStrings(array("$7.00"), Locale.class, map("currencyCode", "USD"), "testExpr");
        assertTrue(Money.valueOfUSD("7.00").equalsExact(value));

        try {
            converter.convertFromStrings(array("a"), Locale.class, map("currencyCode", "USD"), "testExpr");
            fail("Should have failed");
        } catch (ConversionException e) {
        }

        try {
            converter.convertFromStrings(array("a"), Locale.class, map("currencyCode", "BAD"), "testExpr");
            fail("Should have failed");
        } catch (ConverterStateException e) {
        }

        try {
            converter.convertFromStrings(array("7.00"), Locale.class, Collections.<String, String>emptyMap(), "testExpr");
            fail("Should have failed");
        } catch (ConverterStateException e) {
        }
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new MoneyConverter();
        String str = converter.convertToString(null, Money.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString(Money.valueOfUSD("7.00"), Money.class, Collections.<String, String>emptyMap(), "testExpr");
        assertEquals("7.00", str);
    }
}