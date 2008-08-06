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
 * This tests the enum converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class EnumConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        GlobalConverter converter = new EnumConverter();
        TestEnum e = (TestEnum) converter.convertFromStrings(array((String) null), TestEnum.class, null, "testExpr");
        assertNull(e);

        e = (TestEnum) converter.convertFromStrings(array("value1"), TestEnum.class, null, "testExpr");
        assertSame(e, TestEnum.value1);

        e = (TestEnum) converter.convertFromStrings(array("value2"), TestEnum.class, null, "testExpr");
        assertSame(e, TestEnum.value2);

        try {
            converter.convertFromStrings(array("value1", "value2"), TestEnum.class, null, "testExpr");
            fail("Should have failed");
        } catch (UnsupportedOperationException e1) {
            // Expected
        }
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new EnumConverter();
        String str = converter.convertToString(null, TestEnum.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString(TestEnum.value1, TestEnum.class, null, "testExpr");
        assertEquals("value1", str);
    }
}