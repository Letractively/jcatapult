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

import java.util.Locale;

import org.jcatapult.mvc.parameter.convert.GlobalConverter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the locale converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class LocaleConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        GlobalConverter converter = new LocaleConverter();
        Locale locale = (Locale) converter.convertFromStrings(array((String) null), Locale.class, null, "testExpr");
        assertNull(locale);

        locale = (Locale) converter.convertFromStrings(array("en"), Locale.class, null, "testExpr");
        assertEquals("en", locale.getLanguage());

        locale = (Locale) converter.convertFromStrings(array("en_US"), Locale.class, null, "testExpr");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());

        locale = (Locale) converter.convertFromStrings(array("en", "US"), Locale.class, null, "testExpr");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());

        locale = (Locale) converter.convertFromStrings(array("en_US_UTF8"), Locale.class, null, "testExpr");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("UTF8", locale.getVariant());

        locale = (Locale) converter.convertFromStrings(array("en", "US", "UTF8"), Locale.class, null, "testExpr");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("UTF8", locale.getVariant());
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new LocaleConverter();
        String str = converter.convertToString(null, Locale.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString(Locale.US, Locale.class, null, "testExpr");
        assertEquals("en_US", str);
    }
}