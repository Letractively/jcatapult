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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

/**
 * <p>
 * This class is the control for a select box that contains years.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class YearsSelect extends Select {
    /**
     * <p>
     * Adds the years Map and then calls super.
     * </p>
     *
     * @param   attributes The attributes.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    @Override
    protected Map<String, Object> makeParameters(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        Integer start = (Integer) attributes.remove("startYear");
        Integer end = (Integer) attributes.remove("endYear");
        Integer numberOfYears = (Integer) attributes.remove("numberOfYears");
        if (start == null) {
            start = new LocalDate().getYear();
        }

        if (numberOfYears != null) {
            end = start + numberOfYears;
        } else if (end == null) {
            end = start + 10;
        } else if (end != null) {
            end = end + 1;
        }

        List<Integer> years = new ArrayList<Integer>();
        for (int i = start; i < end; i++) {
            years.add(i);
        }

        attributes.put("items", years);

        return super.makeParameters(attributes, dynamicAttributes);
    }
}