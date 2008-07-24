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

import org.jcatapult.action.jcatapult.Months;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This tests the months action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MonthsTest {
    @Test
    public void testAllAlphabetical() {
        Months months = new Months();
        months.execute();
        Map<Integer, String> ms = months.getMonths();
        System.out.println("Result alpha " + ms);

        Iterator<Integer> iter = ms.keySet().iterator();
        int lastCode = iter.next();
        Assert.assertTrue(lastCode > 0 && lastCode < 13);
        while (iter.hasNext()) {
            int code = iter.next();
            Assert.assertTrue(code > 0 && code < 13);
            Assert.assertTrue(lastCode < code);
            lastCode = code;
        }
    }

    @Test
    public void testToday() {
        Months months = new Months();
        months.setStartFromToday(true);
        months.execute();
        Map<Integer, String> ms = months.getMonths();
        System.out.println("Result alpha " + ms);

        Iterator<Integer> iter = ms.keySet().iterator();
        int lastCode = iter.next();
        Assert.assertTrue(lastCode > 0 && lastCode < 13);
        Assert.assertEquals(new LocalDate().getMonthOfYear(), lastCode);
        while (iter.hasNext()) {
            int code = iter.next();
            Assert.assertTrue(code > 0 && code < 13);
            if (code != 1) {
                Assert.assertTrue(lastCode < code);
            }
            lastCode = code;
        }
    }
}