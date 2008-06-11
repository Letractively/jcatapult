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
import org.jcatapult.mvc.parameter.convert.Converter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the number converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class NumberConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        Converter converter = new NumberConverter();
        Byte bw = converter.convertFromStrings(array((String) null), Byte.class, null, null, null);
        assertNull(bw);

        Short sw = converter.convertFromStrings(array((String) null), Short.class, null, null, null);
        assertNull(sw);

        Integer iw = converter.convertFromStrings(array((String) null), Integer.class, null, null, null);
        assertNull(iw);

        Long lw = converter.convertFromStrings(array((String) null), Long.class, null, null, null);
        assertNull(lw);

        Float fw = converter.convertFromStrings(array((String) null), Float.class, null, null, null);
        assertNull(fw);

        Double dw = converter.convertFromStrings(array((String) null), Double.class, null, null, null);
        assertNull(dw);

        byte b = converter.convertFromStrings(array((String) null), Byte.TYPE, null, null, null);
        assertEquals(0, b);

        short s = converter.convertFromStrings(array((String) null), Short.TYPE, null, null, null);
        assertEquals(0, s);

        int i = converter.convertFromStrings(array((String) null), Integer.TYPE, null, null, null);
        assertEquals(0, i);

        long l = converter.convertFromStrings(array((String) null), Long.TYPE, null, null, null);
        assertEquals(0, l);

        float f = converter.convertFromStrings(array((String) null), Float.TYPE, null, null, null);
        assertEquals(0, f, 0);

        double d = converter.convertFromStrings(array((String) null), Double.TYPE, null, null, null);
        assertEquals(0, d, 0);

        b = converter.convertFromStrings(array("1"), Byte.class, null, null, null);
        assertEquals(1, b);

        s = converter.convertFromStrings(array("1"), Short.class, null, null, null);
        assertEquals(1, s);

        i = converter.convertFromStrings(array("1"), Integer.class, null, null, null);
        assertEquals(1, i);

        l = converter.convertFromStrings(array("1"), Long.class, null, null, null);
        assertEquals(1, l);

        f = converter.convertFromStrings(array("1"), Float.class, null, null, null);
        assertEquals(1, f, 0);

        d = converter.convertFromStrings(array("1"), Double.class, null, null, null);
        assertEquals(1, d, 0);

        try {
            converter.convertFromStrings(array("bad"), Byte.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Short.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Integer.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Long.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Float.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Double.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Byte.class, null, null, null);
            fail("Should have failed");
        } catch (ConversionException e) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Byte.TYPE, null, null, null);
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
        Converter converter = new NumberConverter();
        String str = converter.convertToString(null, Integer.class, null, null, null);
        assertNull(str);

        str = converter.convertToString((byte) 42, Byte.class, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString((byte) 42, Byte.TYPE, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString((short) 42, Short.class, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString((short) 42, Short.TYPE, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString(42, Integer.class, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString(42, Integer.class, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString(42l, Long.class, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString(42l, Long.TYPE, null, null, null);
        assertEquals("42", str);

        str = converter.convertToString(42f, Float.class, null, null, null);
        assertEquals("42.0", str);

        str = converter.convertToString(42f, Float.TYPE, null, null, null);
        assertEquals("42.0", str);

        str = converter.convertToString(42.0, Double.class, null, null, null);
        assertEquals("42.0", str);

        str = converter.convertToString(42.0, Double.TYPE, null, null, null);
        assertEquals("42.0", str);
    }
}