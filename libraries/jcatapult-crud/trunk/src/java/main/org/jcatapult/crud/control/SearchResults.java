/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.crud.control;

import org.jcatapult.mvc.result.control.AbstractComponentControl;
import org.jcatapult.mvc.result.control.annotation.ControlAttribute;
import org.jcatapult.mvc.result.control.annotation.ControlAttributes;

/**
 * <p>
 * This is a control for a search result table, pagination and form for
 * deleting entities.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ControlAttributes(
    required = {
        @ControlAttribute(name = "name"),
        @ControlAttribute(name = "properties")
    },
    optional = {
        @ControlAttribute(name = "tableTemplate"),
        @ControlAttribute(name = "controlsTemplate"),
        @ControlAttribute(name = "paginationTemplate"),
        @ControlAttribute(name = "numberPerPageTemplate")
    }
)
public class SearchResults extends AbstractComponentControl {
    /**
     * Removes the properties attribute and re-adds it as an array of Strings by splitting it on commas.
     */
    @Override
    protected void addAdditionalAttributes() {
        String properties = (String) attributes.remove("properties");
        attributes.put("properties", properties.split("\\W*,\\W*"));
    }

    /**
     * @return  Null.
     */
    protected String startTemplateName() {
        return null;
    }

    /**
     * @return  The template name, which defaults to search-results.ftl, but can be set via an attribute
     *          named template.
     */
    protected String endTemplateName() {
        if (attributes.containsKey("template")) {
            return (String) attributes.get("template");
        }
        
        return "search-results.ftl";
    }
}