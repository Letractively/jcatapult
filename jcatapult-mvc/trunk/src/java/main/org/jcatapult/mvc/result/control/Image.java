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
 * This class is the control for an image button.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Image extends AbstractButtonInput {
    /**
     * Calls super and then moves the ismap attribute out and if it is true set it back in as the
     * String <code>ismap</code>.
     *
     * @param   attributes The attributes to check for the ismap attribute.
     * @param   parameterAttributes Not used.
     * @param   actionInvocation Not used.
     */
    @Override
    protected void addAdditionalAttributes(Map<String, Object> attributes,
            Map<String, String> parameterAttributes, ActionInvocation actionInvocation) {
        super.addAdditionalAttributes(attributes, parameterAttributes, actionInvocation);
        Boolean ismap = (Boolean) attributes.remove("ismap");
        if (ismap != null && ismap) {
            attributes.put("ismap", "ismap");
        }
    }

    /**
     * @return  image.ftl
     */
    protected String templateName() {
        return "image.ftl";
    }
}