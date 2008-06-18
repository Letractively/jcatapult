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
package org.jcatapult.mvc.action.result;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerMap implements Map<String, Object> {
    private final ServletContext context;
    private final HttpServletRequest request;
    private final ExpressionEvaluator expressionEvaluator;
    private final Object action;

    public FreeMarkerMap(ServletContext context, HttpServletRequest request, ExpressionEvaluator expressionEvaluator,
            Object action) {
        this.request = request;
        this.expressionEvaluator = expressionEvaluator;
        this.action = action;
        this.context = context;
    }

    public int size() {
        throw new UnsupportedOperationException("Size not known for this map");
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("containsKey not known for this map");
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue not known for this map");
    }

    public Object get(Object key) {
        // First check the action
        Object value = null;
        String strKey = (String) key;
        if (action != null) {
            value = expressionEvaluator.getValue(strKey, action);
        }

        if (value == null) {
            value = request.getAttribute(strKey);
        }

        if (value == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                value = session.getAttribute(strKey);
            }
        }

        if (value == null) {
            value = context.getAttribute(strKey);
        }

        return value;
    }

    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("put not supported for this map");
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException("remove not supported for this map");
    }

    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException("putAll not supported for this map");
    }

    public void clear() {
        throw new UnsupportedOperationException("clear not supported for this map");
    }

    public Set<String> keySet() {
        throw new UnsupportedOperationException("keySet not supported for this map");
    }

    public Collection<Object> values() {
        throw new UnsupportedOperationException("values not supported for this map");
    }

    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("entrySet not supported for this map");
    }
}