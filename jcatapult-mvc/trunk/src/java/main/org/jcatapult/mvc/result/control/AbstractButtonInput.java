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

/**
 * <p>
 * This class is the abstract control for a button input. Button tags are simple
 * in that the value attribute is dynamic and message based.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractButtonInput extends AbstractInput {
    public AbstractButtonInput() {
        super(true);
    }

    /**
     * Calls the super method and then moves the label parameter to the value attribute since the
     * value is viewable by the user.
     *
     * @param   attributes Passed to super.
     * @param   parameterAttributes Passed to super.
     * @param   actionInvocation Passed to super.
     * @param   action Passed to super.
     * @return  The fixed parameters map.
     */
    @Override
    protected Map<String, Object> makeParameters(Map<String, Object> attributes,
            Map<String, String> parameterAttributes, ActionInvocation actionInvocation, Object action) {
        Map<String, Object> parameters = super.makeParameters(attributes, parameterAttributes, actionInvocation, action);
        Object label = parameters.get("label");
        attributes.put("value", label);
        return parameters;
    }
}