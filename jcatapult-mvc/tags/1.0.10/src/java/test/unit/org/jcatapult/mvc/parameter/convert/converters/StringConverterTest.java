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

import org.jcatapult.mvc.parameter.convert.GlobalConverter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the String converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class StringConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        GlobalConverter converter = new StringConverter();
        String str = (String) converter.convertFromStrings(array((String) null), String.class, null, "testExpr");
        assertNull(str);

        str = (String) converter.convertFromStrings(array(""), String.class, null, "testExpr");
        assertNull(str);

        str = (String) converter.convertFromStrings(array("a"), String.class, null, "testExpr");
        assertEquals("a", str);

        str = (String) converter.convertFromStrings(array("a", "b"), String.class, null, "testExpr");
        assertEquals("a,b", str);
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new StringConverter();
        String str = converter.convertToString(null, String.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString("foo", String.class, null, "testExpr");
        assertEquals("foo", str);
    }
}