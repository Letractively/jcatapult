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
package org.jcatapult.mvc.parameters.convert.converters;

import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.Converter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the boolean converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class BooleanConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        Converter converter = new BooleanConverter();
        Boolean b = converter.convertFromStrings(array((String) null), Boolean.class, null, null, null, null);
        assertNull(b);

        b = converter.convertFromStrings(array((String) null), Boolean.TYPE, null, null, null, null);
        assertFalse(b);

        b = converter.convertFromStrings(array("true"), Boolean.class, null, null, null, null);
        assertTrue(b);

        b = converter.convertFromStrings(array("true"), Boolean.TYPE, null, null, null, null);
        assertTrue(b);

        b = converter.convertFromStrings(array("false"), Boolean.class, null, null, null, null);
        assertFalse(b);

        b = converter.convertFromStrings(array("false"), Boolean.TYPE, null, null, null, null);
        assertFalse(b);

        Boolean[] ba = converter.convertFromStrings(array("true", "false"), Boolean[].class, null, null, null, null);
        assertTrue(ba[0]);
        assertFalse(ba[1]);

        boolean[] bpa = converter.convertFromStrings(array("true", "false"), boolean[].class, null, null, null, null);
        assertTrue(bpa[0]);
        assertFalse(bpa[1]);

        try {
            converter.convertFromStrings(array("fals3"), Boolean.class, null, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Boolean.class, null, null, null, null);
            fail("Should have failed");
        } catch (ConversionException e) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Boolean.TYPE, null, null, null, null);
            fail("Should have failed");
        } catch (ConversionException e) {
            // Expected
        }
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        Converter converter = new BooleanConverter();
        String str = converter.convertToString(null, Boolean.class, null, null, null, null);
        assertNull(str);

        str = converter.convertToString(Boolean.TRUE, Boolean.class, null, null, null, null);
        assertEquals("true", str);

        str = converter.convertToString(Boolean.TRUE, Boolean.TYPE, null, null, null, null);
        assertEquals("true", str);

        str = converter.convertToString(Boolean.FALSE, Boolean.class, null, null, null, null);
        assertEquals("false", str);

        str = converter.convertToString(Boolean.FALSE, Boolean.TYPE, null, null, null, null);
        assertEquals("false", str);
    }
}