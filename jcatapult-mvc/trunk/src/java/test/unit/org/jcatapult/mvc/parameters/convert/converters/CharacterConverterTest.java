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
        Converter converter = new CharacterConverter();
        Character cw = converter.convertFromStrings(array((String) null), Character.class, null, null, null, null);
        assertNull(cw);

        char c = converter.convertFromStrings(array((String) null), Character.TYPE, null, null, null, null);
        assertEquals('\u0000', c);

        c = converter.convertFromStrings(array("c"), Character.class, null, null, null, null);
        assertEquals('c', c);

        c = converter.convertFromStrings(array("c"), Character.TYPE, null, null, null, null);
        assertEquals('c', c);

        Character[] ca = converter.convertFromStrings(array("c", "d"), Character[].class, null, null, null, null);
        assertEquals((Character) 'c', ca[0]);
        assertEquals((Character) 'd', ca[1]);

        char[] cpa = converter.convertFromStrings(array("c", "d"), char[].class, null, null, null, null);
        assertEquals('c', cpa[0]);
        assertEquals('d', cpa[1]);

        try {
            converter.convertFromStrings(array("bad"), Character.class, null, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("bad"), Character.TYPE, null, null, null, null);
            fail("Should have failed");
        } catch (ConversionException ce) {
            // Expected
        }

        try {
            converter.convertFromStrings(array("   "), Character.class, null, null, null, null);
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
        String str = converter.convertToString(null, Character.class, null, null, null, null);
        assertNull(str);

        str = converter.convertToString('c', Character.class, null, null, null, null);
        assertEquals("c", str);

        str = converter.convertToString('c', Character.TYPE, null, null, null, null);
        assertEquals("c", str);
    }
}