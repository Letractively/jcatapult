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

import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.GlobalConverter;
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
        GlobalConverter converter = new BooleanConverter();
        Boolean b = (Boolean) converter.convertFromStrings(array((String) null), Boolean.class, null, "testExpr");
        assertNull(b);

        b = (Boolean) converter.convertFromStrings(array((String) null), Boolean.TYPE, null, "testExpr");
        assertFalse(b);

        b = (Boolean) converter.convertFromStrings(array("true"), Boolean.class, null, "testExpr");
        assertTrue(b);

        b = (Boolean) converter.convertFromStrings(array("true"), Boolean.TYPE, null, "testExpr");
        assertTrue(b);

        b = (Boolean) converter.convertFromStrings(array("false"), Boolean.class, null, "testExpr");
        assertFalse(b);

        b = (Boolean) converter.convertFromStrings(array("false"), Boolean.TYPE, null, "testExpr");
        assertFalse(b);

        Boolean[] ba = (Boolean[]) converter.convertFromStrings(array("true", "false"), Boolean[].class, null, "testExpr");
        assertTrue(ba[0]);
        assertFalse(ba[1]);

        boolean[] bpa = (boolean[]) converter.convertFromStrings(array("true", "false"), boolean[].class, null, "testExpr");
        assertTrue(bpa[0]);
        assertFalse(bpa[1]);

        try {
            converter.convertFromStrings(array("fals3"), Boolean.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Boolean.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException e) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Boolean.TYPE, null, "testExpr");
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
        GlobalConverter converter = new BooleanConverter();
        String str = converter.convertToString(null, Boolean.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString(Boolean.TRUE, Boolean.class, null, "testExpr");
        assertEquals("true", str);

        str = converter.convertToString(Boolean.TRUE, Boolean.TYPE, null, "testExpr");
        assertEquals("true", str);

        str = converter.convertToString(Boolean.FALSE, Boolean.class, null, "testExpr");
        assertEquals("false", str);

        str = converter.convertToString(Boolean.FALSE, Boolean.TYPE, null, "testExpr");
        assertEquals("false", str);
    }
}