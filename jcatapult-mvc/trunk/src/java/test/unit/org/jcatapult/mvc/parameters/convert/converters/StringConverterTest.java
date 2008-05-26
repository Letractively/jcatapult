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
 *
 */
package org.jcatapult.mvc.parameters.convert.converters;

import org.jcatapult.mvc.parameters.convert.Converter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the String converter.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class StringConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        Converter converter = new StringConverter();
        String str = converter.convertFromStrings(array((String) null), String.class, null, null, null, null);
        assertNull(str);

        str = converter.convertFromStrings(array(""), String.class, null, null, null, null);
        assertNull(str);

        str = converter.convertFromStrings(array("a"), String.class, null, null, null, null);
        assertEquals("a", str);

        str = converter.convertFromStrings(array("a", "b"), String.class, null, null, null, null);
        assertEquals("a,b", str);
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        Converter converter = new StringConverter();
        String str = converter.convertToString(null, String.class, null, null, null, null);
        assertNull(str);

        str = converter.convertToString("foo", String.class, null, null, null, null);
        assertEquals("foo", str);
    }
}