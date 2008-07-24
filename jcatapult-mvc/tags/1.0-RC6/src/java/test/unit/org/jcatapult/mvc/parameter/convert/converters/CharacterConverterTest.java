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
 * This tests the character converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CharacterConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        GlobalConverter converter = new CharacterConverter();
        Character cw = (Character) converter.convertFromStrings(array((String) null), Character.class, null);
        assertNull(cw);

        char c = (Character) converter.convertFromStrings(array((String) null), Character.TYPE, null);
        assertEquals('\u0000', c);

        c = (Character) converter.convertFromStrings(array("c"), Character.class, null);
        assertEquals('c', c);

        c = (Character) converter.convertFromStrings(array("c"), Character.TYPE, null);
        assertEquals('c', c);

        Character[] ca = (Character[]) converter.convertFromStrings(array("c", "d"), Character[].class, null);
        assertEquals((Character) 'c', ca[0]);
        assertEquals((Character) 'd', ca[1]);

        char[] cpa = (char[]) converter.convertFromStrings(array("c", "d"), char[].class, null);
        assertEquals('c', cpa[0]);
        assertEquals('d', cpa[1]);

        try {
            converter.convertFromStrings(array("bad"), Character.class, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Character.TYPE, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Character.class, null);
            fail("Should have failed");
        } catch (ConversionException e) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Boolean.TYPE, null);
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
        String str = converter.convertToString(null, Character.class, null);
        assertNull(str);

        str = converter.convertToString('c', Character.class, null);
        assertEquals("c", str);

        str = converter.convertToString('c', Character.TYPE, null);
        assertEquals("c", str);
    }
}