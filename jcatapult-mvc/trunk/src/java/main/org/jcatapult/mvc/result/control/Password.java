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

/**
 * <p>
 * This class is the control for a input type=password.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Password extends AbstractInput {
    public Password() {
        super(true);
    }

    /**
     * Removes the value attribute for security.
     *
     * @param   attributes The attributes.
     * @param   parameterAttributes The parameter attributes.
     */
    protected void addAdditionalAttributes(Map<String, Object> attributes, Map<String, String> parameterAttributes
    ) {
        super.addAdditionalAttributes(attributes, parameterAttributes);
        attributes.remove("value");
    }

    /**
     * @return  password.ftl
     */
    protected String endTemplateName() {
        return "password.ftl";
    }
}