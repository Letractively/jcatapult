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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.el.ExpressionException;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;

/**
 * <p>
 * This class is a FreeMarker model that provides access in the templates
 * to the request, session and contet attributes as well as values from the
 * action and the Control directives via the {@link ControlHashModel} class.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerMap implements TemplateHashModelEx {
    private final ServletContext context;
    private final HttpServletRequest request;
    private final ExpressionEvaluator expressionEvaluator;
    private final Object action;
    private final ControlHashModel controlHashModel;

    public FreeMarkerMap(ServletContext context, HttpServletRequest request, ExpressionEvaluator expressionEvaluator,
            Object action, Map<String, Class<? extends TemplateModel>> models, ObjectFactory objectFactory) {
        this.request = request;
        this.expressionEvaluator = expressionEvaluator;
        this.action = action;
        this.context = context;
        this.controlHashModel = new ControlHashModel(objectFactory, models);
    }

    public int size() {
        return controlHashModel.size() + expressionEvaluator.getAllMembers(action.getClass()).size() +
            count(request.getAttributeNames()) + count(request.getSession().getAttributeNames()) +
            count(context.getAttributeNames());
    }

    public boolean isEmpty() {
        return size() > 0;
    }

    public TemplateModel get(String key) {
        if (key.equals("jc")) {
            return controlHashModel;
        }

        // First check the action
        Object value = null;
        if (action != null) {
            try {
                value = expressionEvaluator.getValue(key, action);
            } catch (ExpressionException e) {
                // Smother because the value is probably somewhere else
            }
        }

        if (value == null) {
            value = request.getAttribute(key);
        }

        if (value == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                value = session.getAttribute(key);
            }
        }

        if (value == null) {
            value = context.getAttribute(key);
        }

        return new BeanModel(value, new BeansWrapper());
    }

    public TemplateCollectionModel keys() {
        Set<String> keys = append(controlHashModel.keySet(), iterable(request.getAttributeNames()),
            iterable(request.getSession().getAttributeNames()), iterable(context.getAttributeNames()));
        if (action != null) {
            keys.addAll(expressionEvaluator.getAllMembers(action.getClass()));
        }

        return new SimpleCollection(keys);
    }

    public TemplateCollectionModel values() {
        Collection<Object> values = new ArrayList<Object>(controlHashModel.valueCollection());
        if (action != null) {
            values.addAll(expressionEvaluator.getAllMemberValues(action));
        }

        Enumeration en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            values.add(request.getAttribute(name));
        }

        HttpSession session = request.getSession();
        en = session.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            values.add(session.getAttribute(name));
        }

        en = context.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            values.add(context.getAttribute(name));
        }

        return new SimpleCollection(values);
    }

    private int count(Enumeration enumeration) {
        int count = 0;
        while (enumeration.hasMoreElements()) {
            count++;
            enumeration.nextElement();
        }

        return count;
    }

    private <T> Set<T> append(Iterable<T>... iterables) {
        Set<T> set = new HashSet<T>();
        for (Iterable<T> iterable : iterables) {
            for (T key : iterable) {
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