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
import java.util.Map;

import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the abstract control for the inputs that are checkable
 * such as radio buttons and checkboxes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractCheckedInput extends AbstractInput {
    private final ExpressionEvaluator expressionEvaluator;

    @Inject
    public AbstractCheckedInput(ExpressionEvaluator expressionEvaluator) {
        super(true);
        this.expressionEvaluator = expressionEvaluator;
    }

    /**
     * Adds a boolean attribute named checked if the value associated with the control is equal to
     * the value of the tag.
     *
     * @param   attributes The checked boolean is put into this Map.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    @Override
    protected void addAdditionalAttributes(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        super.addAdditionalAttributes(attributes, dynamicAttributes);
        String name = (String) attributes.get("name");
        Object action = actionInvocation.action();
        if (!attributes.containsKey("checked") && action != null) {
            Object value = expressionEvaluator.getValue(name, action);
            boolean checked = false;
            if (value == null && attributes.containsKey("defaultChecked")) {
                checked = (Boolean) attributes.get("defaultChecked");
            } else if (value != null) {
                // Collection or array. Iterate and toString each item and compare to value
                String valueAttr = (String) attributes.get("value");
                if (value instanceof Iterable) {
                    Iterable iterable = (Iterable) value;
                    for (Object o : iterable) {
                        if (o.toString().equals(valueAttr)) {
                            checked = true;
                            break;
                        }
                    }
                } else if (value.getClass().isArray()) {
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        Object arrayValue = Array.get(value, i);
                        if (arrayValue.toString().equals(valueAttr)) {
                            checked = true;
                            break;
                        }
                    }
                } else {
                    checked = value.toString().equals(valueAttr);
                }
            }

            if (checked) {
                attributes.put("checked", "checked");
            }
        } else if (attributes.containsKey("checked") && (Boolean) attributes.get("checked")) {
            attributes.put("checked", "checked");
        } else {
            attributes.remove("checked");
        }

        attributes.remove("defaultChecked");
    }
}