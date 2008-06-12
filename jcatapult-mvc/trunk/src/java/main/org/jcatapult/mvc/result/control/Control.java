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

import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This interface defines a control is called from a JSP tag
 * library or from a FreeMarker template to render some HTML
 * or other type of output.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Control {
    /**
     * Renders the control.
     *
     * @param   request The servlet request.
     * @param   writer The writer to write the output to.
     * @param   attributes The attributes that are passed from the JSP tag or the FreeMarker
     *          directive.
     * @param   parameterAttributes The parameter attributes that are passed to the tag. These are
     *          described in the class comment of the {@link org.jcatapult.mvc.parameter.ParameterWorkflow}
     *          class. In most cases these are used for type conversion, such as date formats and
     *          currency codes.
     */
    void render(HttpServletRequest request, Writer writer, Map<String, Object> attributes,
            Map<String, String> parameterAttributes);
}