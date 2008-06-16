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
package org.jcatapult.mvc.result.jsp;

import org.jcatapult.mvc.result.control.Control;

/**
 * <p>
 * This class is the abstract tag for Controls that render HTML
 * input and form element tags.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractInputTag<T extends Control> extends AbstractControlTag<T> {
    /**
     * Retrieves the tags disabled attribute
     *
     * @return	Returns the tags disabled attribute
     */
    public Boolean getDisabled() {
        return (Boolean) attributes.get("disabled");
    }

    /**
     * Populates the tags disabled attribute
     *
     * @param	disabled The value of the tags disabled attribute
     */
    public void setDisabled(Boolean disabled) {
        attributes.put("disabled", disabled);
    }

    /**
     * Retrieves the tags readonly attribute
     *
     * @return	Returns the tags readonly attribute
     */
    public Boolean getReadonly() {
        return (Boolean) attributes.get("readonly");
    }

    /**
     * Populates the tags readonly attribute
     *
     * @param	readonly The value of the tags readonly attribute
     */
    public void setReadonly(Boolean readonly) {
        attributes.put("readonly", readonly);
    }

    /**
     * Retrieves the tags size attribute
     *
     * @return	Returns the tags size attribute
     */
    public Integer getSize() {
        return (Integer) attributes.get("size");
    }

    /**
     * Populates the tags size attribute
     *
     * @param	size The value of the tags size attribute
     */
    public void setSize(Integer size) {
        attributes.put("size", size);
    }

    /**
     * Retrieves the tags maxlength attribute
     *
     * @return	Returns the tags maxlength attribute
     */
    public Integer getMaxlength() {
        return (Integer) attributes.get("maxlength");
    }

    /**
     * Populates the tags maxlength attribute
     *
     * @param	maxlength The value of the tags maxlength attribute
     */
    public void setMaxlength(Integer maxlength) {
        attributes.put("maxlength", maxlength);
    }

    /**
     * Retrieves the tags onfocus attribute
     *
     * @return	Returns the tags onfocus attribute
     */
    public String getOnfocus() {
        return (String) attributes.get("onfocus");
    }

    /**
     * Populates the tags onfocus attribute
     *
     * @param	onfocus The value of the tags onfocus attribute
     */
    public void setOnfocus(String onfocus) {
        attributes.put("onfocus", onfocus);
    }

    /**
     * Retrieves the tags onblur attribute
     *
     * @return	Returns the tags onblur attribute
     */
    public String getOnblur() {
        return (String) attributes.get("onblur");
    }

    /**
     * Populates the tags onblur attribute
     *
     * @param	onblur The value of the tags onblur attribute
     */
    public void setOnblur(String onblur) {
        attributes.put("onblur", onblur);
    }

    /**
     * Retrieves the tags onselect attribute
     *
     * @return	Returns the tags onselect attribute
     */
    public String getOnselect() {
        return (String) attributes.get("onselect");
    }

    /**
     * Populates the tags onselect attribute
     *
     * @param	onselect The value of the tags onselect attribute
     */
    public void setOnselect(String onselect) {
        attributes.put("onselect", onselect);
    }

    /**
     * Retrieves the tags onchange attribute
     *
     * @return	Returns the tags onchange attribute
     */
    public String getOnchange() {
        return (String) attributes.get("onchange");
    }

    /**
     * Populates the tags onchange attribute
     *
     * @param	onchange The value of the tags onchange attribute
     */
    public void setOnchange(String onchange) {
        attributes.put("onchange", onchange);
    }

    /**
     * Retrieves the tags value attribute
     *
     * @return	Returns the tags value attribute
     */
    public String getValue() {
        return (String) attributes.get("value");
    }

    /**
     * Populates the tags value attribute
     *
     * @param	value The value of the tags value attribute
     */
    public void setValue(String value) {
        attributes.put("value", value);
    }

    /**
     * Retrieves the tags labelPosition attribute
     *
     * @return	Returns the tags labelPosition attribute
     */
    public String getLabelPosition() {
        return (String) attributes.get("labelPosition");
    }

    /**
     * Populates the tags labelPosition attribute
     *
     * @param	labelPosition The value of the tags labelPosition attribute
     */
    public void setLabelPosition(String labelPosition) {
        attributes.put("labelPosition", labelPosition);
    }

    /**
     * Retrieves the tags bundle attribute.
     *
     * @return	Returns the tags bundle attribute.
     */
    public String getBundle() {
        return (String) attributes.get("bundle");
    }

    /**
     * Populates the tags bundle attribute.
     *
     * @param	bundle The value of the tags bundle attribute.
     */
    public void setBundle(String bundle) {
        attributes.put("bundle", bundle);
    }
}