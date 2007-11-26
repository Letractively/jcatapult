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
package org.jcatapult.struts.convert;

import org.jcatapult.domain.calendar.Month;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the enum type converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class EnumTypeConverterTest {
    @Test
    public void testToString() {
        Month m = Month.APR;
        EnumTypeConverter converter = new EnumTypeConverter();
        String s = (String) converter.convertValue(null, null, null, null, m, String.class);
        assertEquals("APR", s);
    }

    @Test
    public void testFromString() {
        EnumTypeConverter converter = new EnumTypeConverter();
        Month m = (Month) converter.convertValue(null, null, null, null, "APR", Month.class);
        assertEquals(Month.APR, m);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailure() {
        EnumTypeConverter converter = new EnumTypeConverter();
        converter.convertValue(null, null, null, null, "BAD", Month.class);
    }
}