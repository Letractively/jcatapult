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
    /**
     * Retrieves the parameter attributes using the Struts Component stack, which is a stack that
     * contains the current component being rendered if the conversion is occurring on the JSP. Or
     * if this is during a form submission, this uses the attributes put into the ServletRequest by
     * the {@link org.jcatapult.struts.interceptor.JCatapultParametersInterceptor} for the property.
     *
     * @param   context The context used to fetch the component stack.
     * @return  The attributes or an empty Map if there are none.
     */
    protected Map<String, Object> getAttributes(Map<String, Object> context) {
        String fullPropertyName = getPropertyName(context);

        // First check if the field is in the context and this is rendering the JSP
        Map<String, Object> attributes = new HashMap<String, Object>();
        Stack componentStack = (Stack) context.get(Component.COMPONENT_STACK);
        if (componentStack != null) {
            Component component = (Component) componentStack.peek();
            if (component != null && component.getParameters().containsKey("dynamicAttributes")) {
                // Add all the parameters first because FTL files place dynamic attributes in here
                attributes.putAll(component.getParameters());

                // Add the dynamic attributes for JSP tags
                Map<String, Object> dynAttrs = (Map<String, Object>) component.getParameters().get("dynamicAttributes");
                if (dynAttrs != null) {
                    attributes.putAll(dynAttrs);
                }
            }
        }

        Map<String, Object> paramInterceptorAttrs = (Map<String, Object>) ServletObjectsHolder.getServletRequest().
            getAttribute(fullPropertyName + "#attributes");
        if (paramInterceptorAttrs != null) {
            attributes.putAll(paramInterceptorAttrs);
        }

        // Save these off. This is needed for when the form is submitted, goes through the parameters
        // interceptor, gets converted and then validation fails and the form is re-displayed.
        ServletObjectsHolder.getServletRequest().setAttribute(fullPropertyName + "#attributes", attributes);
        return attributes;
    }

    /**
     * Determines the full name of the current property being converted.
     *
     * @param   context The context.
     * @return  The full property name.
     */
    protected String getPropertyName(Map<String, Object> context) {
        String fullPropertyName = (String) context.get("conversion.property.fullName");
        if (fullPropertyName == null) {
            fullPropertyName = (String) context.get("current.property.path");
        }
        return fullPropertyName;
    }
}