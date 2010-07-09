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
        GlobalConverter converter = new NumberConverter();
        Byte bw = (Byte) converter.convertFromStrings(array((String) null), Byte.class, null, "testExpr");
        assertNull(bw);

        Short sw = (Short) converter.convertFromStrings(array((String) null), Short.class, null, "testExpr");
        assertNull(sw);

        Integer iw = (Integer) converter.convertFromStrings(array((String) null), Integer.class, null, "testExpr");
        assertNull(iw);

        Long lw = (Long) converter.convertFromStrings(array((String) null), Long.class, null, "testExpr");
        assertNull(lw);

        Float fw = (Float) converter.convertFromStrings(array((String) null), Float.class, null, "testExpr");
        assertNull(fw);

        Double dw = (Double) converter.convertFromStrings(array((String) null), Double.class, null, "testExpr");
        assertNull(dw);

        byte b = (Byte) converter.convertFromStrings(array((String) null), Byte.TYPE, null, "testExpr");
        assertEquals(0, b);

        short s = (Short) converter.convertFromStrings(array((String) null), Short.TYPE, null, "testExpr");
        assertEquals(0, s);

        int i = (Integer) converter.convertFromStrings(array((String) null), Integer.TYPE, null, "testExpr");
        assertEquals(0, i);

        long l = (Long) converter.convertFromStrings(array((String) null), Long.TYPE, null, "testExpr");
        assertEquals(0, l);

        float f = (Float) converter.convertFromStrings(array((String) null), Float.TYPE, null, "testExpr");
        assertEquals(0, f, 0);

        double d = (Double) converter.convertFromStrings(array((String) null), Double.TYPE, null, "testExpr");
        assertEquals(0, d, 0);

        bw = (Byte) converter.convertFromStrings(array("1"), Byte.class, null, "testExpr");
        assertEquals(1, (byte) bw);

        sw = (Short) converter.convertFromStrings(array("1"), Short.class, null, "testExpr");
        assertEquals(1, (short) sw);

        iw = (Integer) converter.convertFromStrings(array("1"), Integer.class, null, "testExpr");
        assertEquals(1, (int) iw);

        lw = (Long) converter.convertFromStrings(array("1"), Long.class, null, "testExpr");
        assertEquals(1l, (long) lw);

        fw = (Float) converter.convertFromStrings(array("1"), Float.class, null, "testExpr");
        assertEquals(1f, (float) fw, 0);

        dw = (Double) converter.convertFromStrings(array("1"), Double.class, null, "testExpr");
        assertEquals(1d, (double) dw, 0);

        bw = (Byte) converter.convertFromStrings(array("   "), Byte.class, null, "testExpr");
        assertNull(bw);

        sw = (Short) converter.convertFromStrings(array("   "), Short.class, null, "testExpr");
        assertNull(sw);

        iw = (Integer) converter.convertFromStrings(array("   "), Integer.class, null, "testExpr");
        assertNull(iw);

        lw = (Long) converter.convertFromStrings(array("   "), Long.class, null, "testExpr");
        assertNull(lw);

        fw = (Float) converter.convertFromStrings(array("   "), Float.class, null, "testExpr");
        assertNull(fw);

        dw = (Double) converter.convertFromStrings(array("   "), Double.class, null, "testExpr");
        assertNull(dw);

        b = (Byte) converter.convertFromStrings(array("   "), Byte.TYPE, null, "testExpr");
        assertEquals(0, b);

        s = (Short) converter.convertFromStrings(array("   "), Short.TYPE, null, "testExpr");
        assertEquals(0, s);

        i = (Integer) converter.convertFromStrings(array("   "), Integer.TYPE, null, "testExpr");
        assertEquals(0, i);

        l = (Long) converter.convertFromStrings(array("   "), Long.TYPE, null, "testExpr");
        assertEquals(0, l);

        f = (Float) converter.convertFromStrings(array("   "), Float.TYPE, null, "testExpr");
        assertEquals(0, f, 0);

        d = (Double) converter.convertFromStrings(array("   "), Double.TYPE, null, "testExpr");
        assertEquals(0, d, 0);

        try {
            converter.convertFromStrings(array("bad"), Byte.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Short.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Integer.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Long.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Float.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Double.class, null, "testExpr");
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new NumberConverter();
        String str = converter.convertToString(null, Integer.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString((byte) 42, Byte.class, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString((byte) 42, Byte.TYPE, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString((short) 42, Short.class, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString((short) 42, Short.TYPE, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString(42, Integer.class, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString(42, Integer.class, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString(42l, Long.class, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString(42l, Long.TYPE, null, "testExpr");
        assertEquals("42", str);

        str = converter.convertToString(42f, Float.class, null, "testExpr");
        assertEquals("42.0", str);

        str = converter.convertToString(42f, Float.TYPE, null, "testExpr");
        assertEquals("42.0", str);

        str = converter.convertToString(42.0, Double.class, null, "testExpr");
        assertEquals("42.0", str);

        str = converter.convertToString(42.0, Double.TYPE, null, "testExpr");
        assertEquals("42.0", str);
    }
}