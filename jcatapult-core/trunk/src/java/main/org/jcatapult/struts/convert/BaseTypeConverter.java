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
package org.jcatapult.struts.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.struts2.components.Component;
import org.jcatapult.servlet.ServletObjectsHolder;

import ognl.TypeConverter;

/**
 * <p>
 * This class provides access to the parameter attributes that can be
 * used to assist in the conversion process.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class BaseTypeConverter implements TypeConverter {
    protected Map<String, Object> getAttributes(Map<String, Object> context) {
        String fullPropertyName = (String) context.get("current.property.path");
        Map<String, Object> attributes = null;

        // First check if the field is in the context and this is rendering the JSP
        Stack componentStack = (Stack) context.get(Component.COMPONENT_STACK);
        if (componentStack != null) {
            Component component = (Component) componentStack.peek();
            component.
            if (component != null && component.getParameters().containsKey("dynamicAttributes")) {
                attributes = (Map<String, Object>) component.getParameters().get("dynamicAttributes");
            }
        }

        if (attributes == null) {
            attributes = (Map<String, Object>) ServletObjectsHolder.getServletRequest().
                getAttribute(fullPropertyName + "#attributes");
        }

        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        // Save these off. This is needed for when the form is submitted, goes through the parameters
        // interceptor, gets converted and then validation fails and the form is re-displayed.
        ServletObjectsHolder.getServletRequest().setAttribute(fullPropertyName + "#attributes", attributes);
        return attributes;
    }
}