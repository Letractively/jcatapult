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
 *
 */
package org.jcatapult.crud.control;

import org.jcatapult.mvc.result.jsp.AbstractControlTag;

/**
 * <p>
 * This is a JSP tag for a control that displays results, paginates results and
 * provides a form for deleting entities.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SearchResultsTag extends AbstractControlTag<SearchResults> {
    /**
     * @return  The properties to display in the table.
     */
    public String getProperties() {
        return (String) attributes.get("properties");
    }

    /**
     * Sets the properties to display in the table.
     *
     * @param   properties The properties.
     */
    public void setProperties(String properties) {
        attributes.put("properties", properties);
    }

    /**
     * @return  The name of the bean in the action.
     */
    public String getName() {
        return (String) attributes.get("name");
    }

    /**
     * Sets the name of the bean in the action.
     *
     * @param   name The name.
     */
    public void setName(String name) {
        attributes.put("name", name);
    }

    /**
     * The SearchResult.class.
     */
    protected Class<SearchResults> controlClass() {
        return SearchResults.class;
    }
}