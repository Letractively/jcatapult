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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.result.control.Control;

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
    private final ControlMap controlMap;

    public FreeMarkerMap(ServletContext context, HttpServletRequest request, ExpressionEvaluator expressionEvaluator,
            Object action, Map<String, Class<? extends Control>> controls, ObjectFactory objectFactory) {
        this.request = request;
        this.expressionEvaluator = expressionEvaluator;
        this.action = action;
        this.context = context;
        this.controlMap = new ControlMap(objectFactory, controls);
    }

    public int size() {
        return controlMap.size() + expressionEvaluator.getAllMembers(action.getClass()).size() +
            count(request.getAttributeNames()) + count(request.getSession().getAttributeNames()) +
            count(context.getAttributeNames());
    }

    public boolean isEmpty() {
        return size() > 0;
    }

    public boolean containsKey(Object key) {
        return controlMap.containsKey(key) || expressionEvaluator.getAllMembers(action.getClass()).contains(key) ||
            contains(request.getAttributeNames(), key) || contains(request.getSession().getAttributeNames(), key) ||
            contains(context.getAttributeNames(), key);
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue not known for this map");
    }

    public Object get(Object key) {
        if (key.equals("jc")) {
            return controlMap;
        }

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
        return append(controlMap.keySet(), expressionEvaluator.getAllMembers(action.getClass()),
            iterable(request.getAttributeNames()), iterable(request.getSession().getAttributeNames()),
            iterable(context.getAttributeNames()));
    }

    public Collection<Object> values() {
        throw new UnsupportedOperationException("values not supported for this map");
    }

    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("entrySet not supported for this map");
    }

    private int count(Enumeration enumeration) {
        int count = 0;
        while (enumeration.hasMoreElements()) {
            count++;
            enumeration.nextElement();
        }

        return count;
    }

    private boolean contains(Enumeration enumeration, Object key) {
        while (enumeration.hasMoreElements()) {
            if (enumeration.nextElement().equals(key)) {
                return true;
            }
        }

        return false;
    }

    private Set<String> append(Iterable<String>... iterables) {
        Set<String> set = new HashSet<String>();
        for (Iterable<String> iterable : iterables) {
            for (String key : iterable) {
                set.add(key);
            }
        }

        return set;
    }

    private Iterable<String> iterable(Enumeration enumeration) {
        List<String> list = new ArrayList<String>();
        while (enumeration.hasMoreElements()) {
            list.add((String) enumeration.nextElement());
        }

        return list;
    }
}