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

import java.util.Map;
import java.util.Stack;
import java.util.HashMap;

import org.apache.struts2.components.Component;
import org.jcatapult.struts.interceptor.JCatapultParametersInterceptor;

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
        Map<String, Object> attributes = null;

        // First check if the field is in the context and this is rendering the JSP
        Stack componentStack = (Stack) context.get(Component.COMPONENT_STACK);
        if (componentStack != null) {
            Component component = (Component) componentStack.peek();
            if (component != null && component.getParameters().containsKey("dynamicAttributes")) {
                attributes = (Map<String, Object>) component.getParameters().get("dynamicAttributes");
            }
        }

        if (attributes == null) {
            attributes = (Map<String, Object>) context.get(JCatapultParametersInterceptor.ATTRIBUTES);
        }

        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        return attributes;
    }
}