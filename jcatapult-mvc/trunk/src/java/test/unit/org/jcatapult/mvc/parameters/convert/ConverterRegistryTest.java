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
package org.jcatapult.mvc.parameters.convert;

import static org.junit.Assert.*;
import org.junit.Test;

import net.java.convert.Converter;
import net.java.convert.ConverterRegistry;
import net.java.convert.converters.BooleanConverter;
import net.java.convert.converters.CharacterConverter;
import net.java.convert.converters.NumberConverter;
import net.java.convert.converters.StringConverter;

/**
 * <p>
 * This class tests the converter registry
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ConverterRegistryTest {

    /**
     * Test the lookup of converters
     */
    @Test
    public void testLookups() {
        net.java.convert.Converter tc = ConverterRegistry.lookup(Character.class);
        assertSame(CharacterConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Character.TYPE);
        assertSame(CharacterConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Character[].class);
        assertSame(CharacterConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(char[].class);
        assertSame(CharacterConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Byte.class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Byte.TYPE);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Byte[].class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(byte[].class);
        assertSame(NumberConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Short.class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Short.TYPE);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Short[].class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(short[].class);
        assertSame(NumberConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Integer.class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Integer.TYPE);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Integer[].class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(int[].class);
        assertSame(NumberConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Long.class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Long.TYPE);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Long[].class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(long[].class);
        assertSame(NumberConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Float.class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Float.TYPE);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Float[].class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(float[].class);
        assertSame(NumberConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Double.class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Double.TYPE);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Double[].class);
        assertSame(NumberConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(double[].class);
        assertSame(NumberConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(Boolean.class);
        assertSame(BooleanConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Boolean.TYPE);
        assertSame(BooleanConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(Boolean[].class);
        assertSame(BooleanConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(boolean[].class);
        assertSame(BooleanConverter.class, tc.getClass());

        tc = ConverterRegistry.lookup(String.class);
        assertSame(StringConverter.class, tc.getClass());
        tc = ConverterRegistry.lookup(String[].class);
        assertSame(StringConverter.class, tc.getClass());
    }

    /**
     * Tests lookup failures
     */
    @Test
    public void testLookupFailures() {
        Converter tc = ConverterRegistry.lookup(this.getClass());
        assertNull(tc);
    }
}