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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the control for a select box.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Select extends AbstractInput {
    protected ExpressionEvaluator expressionEvaluator;

    @Inject
    public Select(ExpressionEvaluator expressionEvaluator) {
        super(true);
        this.expressionEvaluator = expressionEvaluator;
    }

    /**
     * <p>
     * Handles the items and value. Here's the skinny:
     * </p>
     *
     * <ul>
     * <li>If items is null, just inserts an empty Map in the attributes under <code>options</code></li>
     * <li>If items is a Collection, loops over it and creates options. The selected state of the
     *  options are based on whether or not the value is a Collection or an array or null or just a
     *  plain Object. In the collection/array case, if the current items value is in the collection
     *  the option is selected. In the plain object case, if the current items value is equal it is
     *  selected. Otherwise, it isn't selected. Also, this handles the text and key using the
     *  expression attributes or the current items value.</li>
     * <li>If items is a Map, loops over it and creates options. The selected state of the
     *  options are based on whether or not the value is a Collection or an array or null or just a
     *  plain Object. In the collection/array case, if the current items value is in the collection
     *  the option is selected. In the plain object case, if the current items value is equal it is
     *  selected. Otherwise, it isn't selected. Also, this handles the text and key using the key
     *  from the items Map, the expression attributes or the current items value.</li>
     * </ul>
     *
     * @param   attributes The attributes.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    @Override
    protected Map<String, Object> makeParameters(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        Map<String, Object> parameters = super.makeParameters(attributes, dynamicAttributes);

        // Grab the value
        Object beanValue = action != null ? expressionEvaluator.getValue((String) attributes.get("name"), action) : null;

        // Next, let's handle the items here. I'll create a Map that contains a simple inner class
        // that determines if the option is selected or not. This will allow me to get the text
        // as well
        String keyExpr = (String) attributes.remove("keyExpr");
        String textExpr = (String) attributes.remove("textExpr");
        String l10nExpr = (String) attributes.remove("l10nExpr");
        Map<String, Option> options = new LinkedHashMap<String, Option>();
        Object items = attributes.remove("items");
        if (items != null) {
            if (items instanceof Collection) {
                Collection c = (Collection) items;
                for (Object o : c) {
                    Object key = makeKey(o, null, keyExpr);
                    options.put(key.toString(), makeOption(o, key, beanValue, attributes, textExpr, l10nExpr));
                }
            } else if (items instanceof Map) {
                Map<?,?> m = (Map<?,?>) items;
                for (Map.Entry entry : m.entrySet()) {
                    Object key = makeKey(entry.getValue(), entry.getKey(), keyExpr);
                    Option option = makeOption(entry.getValue(), key, beanValue, attributes, textExpr, l10nExpr);
                    options.put(key.toString(), option);
                }
            } else if (items.getClass().isArray()) {
                int length = Array.getLength(items);
                for (int i = 0; i < length; i++) {
                    Object itemsValue = Array.get(items, i);
                    Object key = makeKey(itemsValue, null, keyExpr);
                    Option option = makeOption(itemsValue, key, beanValue, attributes, textExpr, l10nExpr);
                    options.put(key.toString(), option);
                }
            }
        }

        parameters.put("options", options);
        return parameters;
    }

    /**
     * Makes an option. If the attributes contains a <code>l10nExpr</code>, it is used with the Object
     * to get a message from the {@link org.jcatapult.l10n.MessageProvider}. If that doesn't
     * exist and a <code>textExpr</code> does, it is used to get the text for the option from the
     * object. Otherwise, the object is converted to a String for the text. Also, if the object exists
     * in the given Collection the option is set to selected.
     *
     * @param   itemsValue The current value from the items collection/array/map.
     * @param   key The key that is the value of the option. This could have been from the items Map
     *          or the keyExpr evaluation.
     * @param   beanValue The value from the bean, used to determine selected state.
     * @param   attributes used to get the text for the option.
     * @param   textExpr The textExpr attribute.
     * @param   l10nExpr The l10nExpr attribute.
     * @return  The option and never null.
     */
    private Option makeOption(Object itemsValue, Object key, Object beanValue, Map<String, Object> attributes,
            String textExpr, String l10nExpr) {
        if (itemsValue == null) {
            return new Option("", false);
        }

        String text = null;
        if (l10nExpr != null) {
            String bundleName = determineBundleName(attributes);
            if (bundleName == null) {
                throw new IllegalStateException("Unable to locate the localized text for an option " +
                    "in the select input for the field named [" + attributes.get("name") + "]. If " +
                    "you don't have an action class for the URL, you must define the bundle used " +
                    "to localize the select using the bundle attribute.");
            }

            Object l10nKey = expressionEvaluator.getValue(l10nExpr, itemsValue);
            if (l10nKey != null) {
                text = messageProvider.getMessage(bundleName, l10nKey.toString());
            }
        }

        if (text == null) {
            if (textExpr != null) {
                text = expressionEvaluator.getValue(textExpr, itemsValue).toString();
            }
        }

        if (text == null) {
            text = itemsValue.toString();
        }

        if (beanValue == null) {
            return new Option(text, false);
        }

        if (beanValue instanceof Collection) {
            return new Option(text, ((Collection) beanValue).contains(itemsValue));
        }

        if (beanValue.getClass().isArray()) {
            int length = Array.getLength(beanValue);
            for (int i = 0; i < length; i++) {
                Object arrayValue = Array.get(beanValue, i);
                if (arrayValue != null && arrayValue.equals(itemsValue)) {
                    return new Option(text, true);
                }
            }
        }

        if (key != null) {
            return new Option(text, beanValue.equals(key));
        }

        return new Option(text, beanValue.equals(itemsValue));
    }

    /**
     * Determines the key. If the attribute contains a <code>keyExpr</code>, it is used against the
     * object to get the key. Otherwise, the object is just converted to a String.
     *
     * @param   itemsValue The current value from the items collection/array/map used to determine the key.
     * @param   key The key from a items Map or null if items is not a Map.
     * @param   keyExpr The keyExpr attribute.
     * @return  The key and never null. If the Object is null, this returns an empty String.
     */
    private Object makeKey(Object itemsValue, Object key, String keyExpr) {
        if (itemsValue == null) {
            return "";
        }

        if (keyExpr != null) {
            Object keyValue = expressionEvaluator.getValue(keyExpr, itemsValue);
            if (keyValue != null) {
                return keyValue;
            }
        }

        if (key != null) {
            return key;
        }

        return itemsValue;
    }

    /**
     * @return  select.ftl
     */
    protected String endTemplateName() {
        return "select.ftl";
    }

    public static class Option {
        private final String text;
        private final boolean selected;

        public Option(String text, boolean selected) {
            this.text = text;
            this.selected = selected;
        }

        public String getText() {
            return text;
        }

        public boolean isSelected() {
            return selected;
        }
    }
}