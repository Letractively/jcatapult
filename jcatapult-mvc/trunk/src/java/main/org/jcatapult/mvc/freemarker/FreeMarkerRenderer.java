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
package org.jcatapult.mvc.freemarker;

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * This interface defines the method by which FreeMarker templates
 * are rendered for the MVC. This includes FreeMarker results as
 * well as FreeMarker templates used by the JSP tag library and
 * FreeMarker directives.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface FreeMarkerRenderer {
    /**
     * Renders the given FreeMarker template with the given parameters and locale.
     *
     * @param   stream The stream that the rendered template is output to.
     * @param   template The template.
     * @param   parameters The parameters.
     * @param   locale The locale.
     */
    void render(OutputStream stream, String template, Map<String, Object> parameters, Locale locale);
}