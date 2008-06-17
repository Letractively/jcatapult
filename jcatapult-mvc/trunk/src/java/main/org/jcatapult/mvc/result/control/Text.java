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
package org.jcatapult.mvc.result.control;

import java.util.Map;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the control for a input type=text.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Text extends AbstractInput {
    private final ExpressionEvaluator expressionEvaluator;

    @Inject
    public Text(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    /**
     * Adds a String attribute named <strong>value</strong> by pulling the value associated with the
     * control. However, if there is already a value attribute, it is always used. Likewise, if the
     * value attribute is missing, the value associated with the control is null and there is a
     * <strong>defaultValue</strong> attribute, it is used.
     *
     * @param   attributes The value String is put into this Map.
     * @param   actionInvocation Used to grab the action.
     */
    protected void addAdditionalAttributes(Map<String, Object> attributes,
            Map<String, String> parameterAttributes, ActionInvocation actionInvocation) {
        // Call super to handle the ID
        super.addAdditionalAttributes(attributes, parameterAttributes, actionInvocation);

        String name = (String) attributes.get("name");
        Object action = actionInvocation.action();
        String value;
        if (!attributes.containsKey("value") && action != null) {
            value = expressionEvaluator.getValue(name, action, parameterAttributes);
            if (value == null) {
                value = (String) attributes.remove("defaultValue");
            }

            if (value != null) {
                attributes.put("value", value);
            }
        }
    }

    /**
     * @return  text.ftl
     */
    protected String templateName() {
        return "text.ftl";
    }
}