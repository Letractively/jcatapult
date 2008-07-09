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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * This class is the control for a select box.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CountriesSelect extends Select {
    /**
     * <p>
     * Adds the countries Map and then calls super.
     * </p>
     *
     * @param   attributes The attributes.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    @Override
    protected Map<String, Object> makeParameters(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        Map<String, String> countries = new TreeMap<String, String>();
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale l : locales) {
            countries.put(l.getISO3Country(), l.getDisplayCountry(locale));
        }

        if (attributes.containsKey("includeBlank") && (Boolean) attributes.get("includeBlank")) {
            countries.put("", "");
        }

        String preferred = (String) attributes.get("preferredCodes");
        if (preferred != null) {
            String[] parts = preferred.split(",");
            Map<String, String> newCountries = new LinkedHashMap<String, String>();
            for (String part : parts) {
                newCountries.put(part.trim(), countries.remove(part.trim()));
            }

            newCountries.putAll(countries);
            countries = newCountries;
        }

        attributes.put("items", countries);

        return super.makeParameters(attributes, dynamicAttributes);
    }
}