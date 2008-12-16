/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.crud.control;

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