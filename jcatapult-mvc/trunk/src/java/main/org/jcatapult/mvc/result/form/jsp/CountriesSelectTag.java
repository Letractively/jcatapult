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
package org.jcatapult.mvc.result.form.jsp;

import org.jcatapult.mvc.result.form.control.CountriesSelect;

/**
 * <p>
 * This class is the JSP taglib for the countries select control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CountriesSelectTag extends SelectTag<CountriesSelect> {
    /**
     * Retrieves the tags includeblank attribute
     *
     * @return	Returns the tags includeblank attribute
     */
    public Boolean getIncludeblank() {
        return (Boolean) attributes.get("includeblank");
    }

    /**
     * Populates the tags includeblank attribute
     *
     * @param	includeblank The value of the tags includeblank attribute
     */
    public void setIncludeblank(Boolean includeblank) {
        attributes.put("includeblank", includeblank);
    }

    /**
     * Retrieves the tags preferredcodes attribute
     *
     * @return	Returns the tags preferredcodes attribute
     */
    public String getPreferredcodes() {
        return (String) attributes.get("preferredcodes");
    }

    /**
     * Populates the tags preferredcodes attribute
     *
     * @param	preferredcodes The value of the tags preferredcodes attribute
     */
    public void setPreferredcodes(String preferredcodes) {
        attributes.put("preferredcodes", preferredcodes);
    }

    /**
     * @return  The {@link CountriesSelect} class.
     */
    protected Class<CountriesSelect> controlClass() {
        return CountriesSelect.class;
    }
}