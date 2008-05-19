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
package org.jcatapult.action;

import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This tests the countries action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CountriesTest {
    @Test
    public void testAllAlphabetical() {
        Countries countries = new Countries();
        countries.execute();
        Map<String, String> map = countries.getCountries();
        System.out.println("Result alpha " + map);

        Iterator<String> iter = map.keySet().iterator();
        String lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        while (iter.hasNext()) {
            String code = iter.next();
            Assert.assertTrue(map.get(lastCode).compareTo(map.get(code)) < 0);
            Assert.assertEquals(2, code.length());
            lastCode = code;
        }
    }

    @Test
    public void testPreferred() {
        Countries countries = new Countries();
        countries.setPreferredCodes("US, AE, DE");
        countries.execute();
        Map<String, String> map = countries.getCountries();
        System.out.println("Result pref " + map);

        Iterator<String> iter = map.keySet().iterator();
        String lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        Assert.assertEquals("US", lastCode);
        Assert.assertEquals("United States", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        Assert.assertEquals("AE", lastCode);
        Assert.assertEquals("United Arab Emirates", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        Assert.assertEquals("DE", lastCode);
        Assert.assertEquals("Germany", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        while (iter.hasNext()) {
            String code = iter.next();
            Assert.assertTrue(map.get(lastCode).compareTo(map.get(code)) < 0);
            Assert.assertEquals(2, code.length());
            lastCode = code;
        }
    }

    @Test
    public void testBlankNoPreferred() {
        Countries countries = new Countries();
        countries.setBlankValue("--");
        countries.setIncludeBlank(true);
        countries.execute();
        Map<String, String> map = countries.getCountries();
        System.out.println("Result blank " + map);

        Iterator<String> iter = map.keySet().iterator();
        String lastCode = iter.next();
        Assert.assertEquals(0, lastCode.length());
        Assert.assertEquals("", lastCode);
        Assert.assertEquals("--", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        while (iter.hasNext()) {
            String code = iter.next();
            Assert.assertTrue(map.get(lastCode).compareTo(map.get(code)) < 0);
            Assert.assertEquals(2, code.length());
            lastCode = code;
        }
    }

    @Test
    public void testBlankWithPreferred() {
        Countries countries = new Countries();
        countries.setBlankValue("--");
        countries.setIncludeBlank(true);
        countries.setPreferredCodes("US,AE,DE");
        countries.execute();
        Map<String, String> map = countries.getCountries();
        System.out.println("Result blank pref" + map);

        Iterator<String> iter = map.keySet().iterator();
        String lastCode = iter.next();
        Assert.assertEquals(0, lastCode.length());
        Assert.assertEquals("", lastCode);
        Assert.assertEquals("--", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        Assert.assertEquals("US", lastCode);
        Assert.assertEquals("United States", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        Assert.assertEquals("AE", lastCode);
        Assert.assertEquals("United Arab Emirates", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        Assert.assertEquals("DE", lastCode);
        Assert.assertEquals("Germany", map.get(lastCode));

        lastCode = iter.next();
        Assert.assertEquals(2, lastCode.length());
        while (iter.hasNext()) {
            String code = iter.next();
            Assert.assertTrue(map.get(lastCode).compareTo(map.get(code)) < 0);
            Assert.assertEquals(2, code.length());
            lastCode = code;
        }
    }
}