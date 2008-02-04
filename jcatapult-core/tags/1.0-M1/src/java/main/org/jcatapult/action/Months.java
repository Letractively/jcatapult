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

import java.util.LinkedHashMap;
import java.util.Locale;

import org.joda.time.LocalDate;

import com.opensymphony.xwork2.Action;

/**
 * <p>
 * This class is an action that can be used from forms to build a
 * Map of months. The key in the map is the integer value of the
 * Month and the value is the String name of the month.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Months implements Action {
    private boolean startFromToday;
    private Locale displayLocale;
    private int length = 5;
    private LinkedHashMap<Integer, String> months = new LinkedHashMap<Integer, String>();

    /**
     * This parameter determines if the Map of months should start from January or from the current
     * month.
     *
     * @param   startFromToday If this is true, the Map of months starts from the current month and
     *          wraps around to last month.
     */
    public void setStartFromToday(boolean startFromToday) {
        this.startFromToday = startFromToday;
    }

    /**
     * This parameter sets the display locale for the month names.
     *
     * @param   displayLocale The display locale.
     */
    public void setDisplayLocale(Locale displayLocale) {
        this.displayLocale = displayLocale;
    }

    /**
     * The number of characters to display in the month name.
     *
     * @param   length The number of characters to display in the month name (anything over 4 is the
     *          full month name).
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return  The months or null if execute hasn't been called.
     */
    public LinkedHashMap<Integer, String> getMonths() {
        return months;
    }

    public String execute() {
        int start = 1;
        LocalDate now = new LocalDate();
        if (startFromToday) {
            start = now.getMonthOfYear();
        }

        String format = "";
        for (int i = 0; i < length; i++) {
            format += "M";
        }

        for (int i = 0; i < 12; i++) {
            int monthOfYear = (start + i) % 13;
            if (monthOfYear == 0) {
                start++;
                monthOfYear = 1;
            }

            LocalDate month = new LocalDate(now.getYear(), monthOfYear, 1);
            String str = month.toString(format, displayLocale);
            months.put(monthOfYear, str);
        }

        return SUCCESS;
    }
}