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

import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;

import org.jcatapult.mvc.result.control.Select;

/**
 * <p>
 * This class is the JSP taglib for the select control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SelectTag extends AbstractInputTag<Select> {
    /**
     * Retrieves the tags multiple attribute
     *
     * @return	Returns the tags multiple attribute
     */
    public Boolean getMultiple() {
        return (Boolean) attributes.get("multiple");
    }

    /**
     * Populates the tags multiple attribute
     *
     * @param	multiple The value of the tags multiple attribute
     */
    public void setMultiple(Boolean multiple) {
        attributes.put("multiple", multiple);
    }

    /**
     * Retrieves the tags items, which might be a Collection or a Map.
     *
     * @return	Returns the tags items
     */
    public Object getItems() {
        return attributes.get("items");
    }

    /**
     * Populates the tags items, which might be a Collection or a Map.
     *
     * @param	items The value of the tags items
     * @throws  JspException If the items is not an array, Collection or Map.
     */
    public void setItems(Object items) throws JspException {
        if (!(items instanceof Collection) && !(items instanceof Map) &&
                (items != null && items.getClass().isArray())) {
            throw new JspException("Invalid items list. Must be an array, Collection or Map.");
        }
        
        attributes.put("items", items);
    }

    /**
     * Retrieves the tags valueExpr attribute. This is used to generate the value of the option tags
     * for the select box. This is used inconjunction with the items list. If the items are not
     * specified, then this attribute is ignored.
     *
     * @return	Returns the tags valueExpr attribute.
     */
    public String getValueExpr() {
        return (String) attributes.get("valueExpr");
    }

    /**
     * Populates the tags valueExpr attribute. This is used to generate the value of the option tags
     * for the select box. This is used inconjunction with the items list. If the items are not
     * specified, then this attribute is ignored.
     *
     * @param	valueExpr The value of the tags valueExpr attribute
     */
    public void setValueExpr(String valueExpr) {
        attributes.put("valueExpr", valueExpr);
    }

    /**
     * Retrieves the tags textExpr attribute. This is used to generate the text of the option tags
     * for the select box (which is the body of the option tag). This is used inconjunction with the
     * items list. If the items are not specified, then this attribute is ignored. If the items are
     * specified, this attribute should contain an expression that is evaluated to produce the text
     * for the option.
     *
     * @return	Returns the tags textExpr attribute.
     */
    public String getTextExpr() {
        return (String) attributes.get("textExpr");
    }

    /**
     * Populates the tags textExpr attribute. This is used to generate the text of the option tags
     * for the select box (which is the body of the option tag). This is used inconjunction with the
     * items list. If the items are not specified, then this attribute is ignored. If the items are
     * specified, this attribute should contain an expression that is evaluated to produce the text
     * for the option.
     *
     * @param	textExpr The value of the tags textExpr attribute
     */
    public void setTextExpr(String textExpr) {
        attributes.put("textExpr", textExpr);
    }

    /**
     * Retrieves the tags l10nExpr attribute. This is used to generate the text of the option tags
     * for the select box (which is the body of the option tag). This is used inconjunction with the
     * items list. If the items are not specified, then this attribute is ignored. If the list is
     * specified, this attribute should contain an expression that is evaluated to produce a
     * localization key. This key is used with the {@link org.jcatapult.mvc.message.MessageProvider}
     * to look up the text for the option.
     *
     * @return	Returns the tags l10nExpr attribute.
     */
    public String getL10nExpr() {
        return (String) attributes.get("l10nExpr");
    }

    /**
     * Populates the tags l10nExpr attribute. This is used to generate the text of the option tags
     * for the select box (which is the body of the option tag). This is used inconjunction with the
     * items list. If the items are not specified, then this attribute is ignored. If the list is
     * specified, this attribute should contain an expression that is evaluated to produce a
     * localization key. This key is used with the {@link org.jcatapult.mvc.message.MessageProvider}
     * to look up the text for the option.
     *
     * @param	l10nExpr The value of the tags l10nExpr attribute
     */
    public void setL10nExpr(String l10nExpr) {
        attributes.put("l10nExpr", l10nExpr);
    }

    /**
     * @return  The {@link Select} class.
     */
    protected Class<Select> controlClass() {
        return Select.class;
    }
}