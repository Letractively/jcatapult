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
 * This class is the control for a checkbox.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Checkbox extends AbstractInput {
    private final ExpressionEvaluator expressionEvaluator;

    @Inject
    public Checkbox(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    /**
     * Adds a boolean attribute named checked if the value associated with the control is equal to
     * the value of the tag.
     *
     * @param   attributes The checked boolean is put into this Map.
     * @param   actionInvocation Used to grab the action.
     */
    @Override
    protected void addAdditionalAttributes(Map<String, Object> attributes,
            Map<String, String> parameterAttributes, ActionInvocation actionInvocation) {
        String name = (String) attributes.get("name");
        Object action = actionInvocation.action();
        if (!attributes.containsKey("checked") && action != null) {
            String value = expressionEvaluator.getValue(name, action, parameterAttributes);
            Boolean checked = (value == null) ? (Boolean) attributes.get("defaultChecked") :
                value.equals(attributes.get("value"));
            if (checked != null) {
                attributes.put("checked", checked);
            }
        }
    }

    /**
     * @return  checkbox.ftl
     */
    protected String templateName() {
        return "checkbox.ftl";
    }
}