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

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.opensymphony.xwork2.Action;
import net.java.lang.StringTools;

/**
 * <p>
 * This class is a Struts action that can be called from forms or
 * chained in order to build a Map of countries. This Map is a linked
 * Map so that the countries are in a specific order. By default this
 * order is alphabetical. In addition, you can set the preferred set
 * of countries that go at the top using the {@link #setPreferredCodes(String)}
 * method.
 * </p>
 *
 * <p>
 * The Map uses the country codes as the key and the name of the
 * country as the value.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Countries implements Action {
    private boolean includeBlank;
    private String blankValue;
    private Locale displayLocale;
    private String[] preferredCodes;
    private Map<String, String> countries;

    /**
     * This parameters determines if a blank entry is added to the front of the linked Map that is
     * built by the {@link #execute()} method. The blank key will always be empty String and the value
     * will default to empty String unless set by the {@link #setBlankValue(String)} method.
     *
     * @param   includeBlank If true a blank will be inserted, false it will not. This defaults to
     *          false.
     */
    public void setIncludeBlank(boolean includeBlank) {
        this.includeBlank = includeBlank;
    }

    /**
     * This parameter is the value that will be used for the blank spot if the {@link #setIncludeBlank(boolean)}
     * parameter is set to true. The key will always empty String.
     *
     * @param   blankValue The value for the blank spot.
     */
    public void setBlankValue(String blankValue) {
        this.blankValue = blankValue;
    }

    /**
     * This parameters determines what the display locale for the Country names is. This allows the
     * resulting Map to be localized to the display Locale.
     *
     * @param   displayLocale The Locale used for the country names which are the values in the Map.
     *          This defaults to the system default Locale.
     */
    public void setDisplayLocale(Locale displayLocale) {
        this.displayLocale = displayLocale;
    }

    /**
     * This parameter can be passed in in order to set up the countries that should be at the top of
     * the Map. These are the country codes and the order they are given is the order that they are
     * put into the linked Map.
     *
     * @param   preferredCodes The preferred country codes.
     */
    @SuppressWarnings({"unchecked"})
    public void setPreferredCodes(String preferredCodes) {
        this.preferredCodes = preferredCodes.split("\\W*,\\W*");
    }

    /**
     * The Map built by the {@link #execute()} method.
     *
     * @return  The Map or null if execute was not called.
     */
    public Map<String, String> getCountries() {
        return countries;
    }

    /**
     * Builds the Map of countries.
     *
     * @return  Always success.
     */
    public String execute() {
        Locale displayLocale = this.displayLocale != null ? this.displayLocale : Locale.getDefault();

        SortedSet<Locale> alphabetical = new TreeSet<Locale>(new LocaleComparator(displayLocale));
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (!StringTools.isTrimmedEmpty(locale.getCountry()) &&
                    !StringTools.isTrimmedEmpty(locale.getDisplayCountry(displayLocale))) {
                alphabetical.add(locale);
            }
        }

        countries = new LinkedHashMap<String, String>();

        if (includeBlank) {
            countries.put("", blankValue == null ? "" : blankValue);
        }

        if (preferredCodes != null && preferredCodes.length > 0) {
            for (String preferCode : preferredCodes) {
                Locale locale = new Locale("", preferCode);
                countries.put(preferCode, locale.getDisplayCountry(displayLocale));
            }
        }

        for (Locale locale : alphabetical) {
            if (!countries.containsKey(locale.getCountry())) {
                countries.put(locale.getCountry(), locale.getDisplayCountry(displayLocale));
            }
        }

        return SUCCESS;
    }

    public static class LocaleComparator implements Comparator<Locale> {
        private Locale displayLocale;

        public LocaleComparator(Locale displayLocale) {
            this.displayLocale = displayLocale;
        }

        public int compare(Locale l1, Locale l2) {
            return l1.getDisplayCountry(displayLocale).compareTo(l2.getDisplayCountry(displayLocale));
        }
    }
}