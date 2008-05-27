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
 *
 */
package org.jcatapult.mvc.parameters;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.servlet.Workflow;

/**
 * <p>
 * This interface marks a class as the parameter handling part of the MVC
 * workflow. This part is how the JCatapult MVC pulls HTTP request
 * parameters and set them into actions.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface ParameterWorkflow extends Workflow {
    /**
     * Pulls the attributes for the given parameter out of the request.
     *
     * @param   request The request.
     * @param   parameter The name of the parameter.
     * @return  The attributes or an empty Map if the parameter has no attributes.
     */
    Map<String, String> fetchAttributes(HttpServletRequest request, String parameter);
}