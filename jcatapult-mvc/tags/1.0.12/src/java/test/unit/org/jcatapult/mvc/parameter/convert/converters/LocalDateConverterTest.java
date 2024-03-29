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

import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.GlobalConverter;
import org.joda.time.LocalDate;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the local date converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class LocalDateConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        GlobalConverter converter = new LocalDateConverter();
        LocalDate value = (LocalDate) converter.convertFromStrings(array((String) null), LocalDate.class, null, "testExpr");
        assertNull(value);

        value = (LocalDate) converter.convertFromStrings(array("07-08-2008"), Locale.class, map("dateTimeFormat", "MM-dd-yyyy"), "testExpr");
        assertEquals(7, value.getMonthOfYear());
        assertEquals(8, value.getDayOfMonth());
        assertEquals(2008, value.getYear());

        try {
            converter.convertFromStrings(array("07/08/2008"), Locale.class, map("dateTimeFormat", "MM-dd-yyyy"), "testExpr");
            fail("Should have failed");
        } catch (ConversionException e) {
        }
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new LocalDateConverter();
        String str = converter.convertToString(null, LocalDate.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString(new LocalDate(2008, 7, 8), LocalDate.class, map("dateTimeFormat", "MM-dd-yyyy"), "testExpr");
        assertEquals("07-08-2008", str);
    }
}