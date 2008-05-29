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
package org.jcatapult.action.jcatapult;

import java.util.LinkedHashMap;

import org.joda.time.LocalDate;

import com.opensymphony.xwork2.Action;

/**
 * <p>
 * This class is an action that can be used from forms to build a
 * Map of years. The key in the map is the integer value of the
 * year and the value is the same.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Years implements Action {
    private Integer startYear;
    private int number = 10;
    private LinkedHashMap<Integer, String> years = new LinkedHashMap<Integer, String>();

    /**
     * This parameter determines the start year. This might be this year or a previous or future year.
     * This defaults to this year.
     *
     * @param   startYear The start year.
     */
    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    /**
     * Sets the number of years to include. This defaults to 10.
     *
     * @param   number The number of years.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return  The years or null if execute hasn't been called.
     */
    public LinkedHashMap<Integer, String> getYears() {
        return years;
    }

    public String execute() {
        LocalDate now = new LocalDate();
        int start = now.getYear();
        if (startYear != null) {
            start = startYear;
        }

        for (int i = 0; i < number; i++) {
            years.put(start + i, "" + (start + i));
        }

        return SUCCESS;
    }
}