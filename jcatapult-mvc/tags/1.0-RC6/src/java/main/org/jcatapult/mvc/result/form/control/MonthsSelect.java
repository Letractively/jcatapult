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
package org.jcatapult.mvc.result.form.control;

import java.util.Map;
import java.util.TreeMap;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * <p>
 * This class is the control for a select box that contains months.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MonthsSelect extends Select {
    /**
     * <p>
     * Adds the months Map and then calls super.
     * </p>
     *
     * @param   attributes The attributes.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    @Override
    protected Map<String, Object> makeParameters(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        Map<Integer, String> months = new TreeMap<Integer, String>();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM").withLocale(locale);
        for (int i = 1; i <= 12; i++) {
            LocalDate date = new LocalDate(2008, i, 1);
            months.put(i, formatter.print(date));
        }

        attributes.put("items", months);

        return super.makeParameters(attributes, dynamicAttributes);
    }
}