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
package org.jcatapult.mvc.action.result.freemarker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.action.result.ControlHashModel;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.el.ExpressionException;

import com.google.inject.Inject;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * <p>
 * This class is a FreeMarker model that provides access in the templates
 * to the request, session and contet attributes as well as values from the
 * action and the Control directives via the {@link org.jcatapult.mvc.action.result.ControlHashModel} class.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerMap implements TemplateHashModelEx {
    private static final String REQUEST_MODEL = "Request";
    private static final String REQUEST = "request";
    private static final String SESSION_MODEL = "Session";
    private static final String SESSION = "session";
    private static final String APPLICATION_MODEL = "Application";
    private static final String APPLICATION = "application";
    private static final String JCATAPULT_TAGS = "jc";
    private static final String JSP_TAGLIBS = "JspTaglibs";

    private static final Map<String, Class<? extends TemplateModel>> models = new HashMap<String, Class<? extends TemplateModel>>();
    private static TaglibFactory taglibFactory;
    private static ObjectFactory objectFactory;

    private static ServletContext context;
    private final Map<String, Object> objects = new HashMap<String, Object>();
    private final HttpServletRequest request;
    private final ExpressionEvaluator expressionEvaluator;
    private final Object action;

    /**
     * Initializes the ServletContext and the JSP taglib support for FreeMaker and also the TemplateModel
     * classes that are bound into the ObjectFactory.
     *
     * @param   context The context.
     * @param   objectFactory Used to get the template models.
     */
    @Inject
    public static void initialize(ServletContext context, ObjectFactory objectFactory) {
        FreeMarkerMap.taglibFactory = new TaglibFactory(context);
        FreeMarkerMap.context = context;

        List<Class<? extends TemplateModel>> types = objectFactory.getAllForType(TemplateModel.class);
        for (Class<? extends TemplateModel> type : types) {
            models.put(type.getSimpleName().toLowerCase(), type);
        }

        FreeMarkerMap.objectFactory = objectFactory;
    }

    public FreeMarkerMap(HttpServletRequest request, HttpServletResponse response,
            ExpressionEvaluator expressionEvaluator, Object action, Map<String, Object> additionalValues) {
        objects.put(REQUEST_MODEL, new HttpRequestHashModel(request, response, ObjectWrapper.DEFAULT_WRAPPER));
        objects.put(REQUEST, request);
        HttpSession session = request.getSession(false);
        if (session != null) {
            objects.put(SESSION_MODEL, new HttpSessionHashModel(session, ObjectWrapper.DEFAULT_WRAPPER));
            objects.put(SESSION, session);
        }
        objects.put(APPLICATION_MODEL, new ServletContextHashModel(new GenericServlet() {
            public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
            }

            @Override
            public ServletConfig getServletConfig() {
                return this;
            }

            @Override
            public ServletContext getServletContext() {
                return context;
            }


        }, ObjectWrapper.DEFAULT_WRAPPER));
        objects.put(APPLICATION, context);
        objects.put(JSP_TAGLIBS, taglibFactory);
        objects.put(JCATAPULT_TAGS, new ControlHashModel(objectFactory, models));

        objects.putAll(additionalValues);

        this.request = request;
        this.expressionEvaluator = expressionEvaluator;
        this.action = action;
    }

    public int size() {
        return objects.size() + expressionEvaluator.getAllMembers(action.getClass()).size() +
            count(request.getAttributeNames()) + count(request.getSession().getAttributeNames()) +
            count(context.getAttributeNames());
    }

    public boolean isEmpty() {
        return size() > 0;
    }

    public TemplateModel get(String key) {
        // First check the action
        Object value = null;
        if (action != null) {
            try {
                value = expressionEvaluator.getValue(key, action);
            } catch (ExpressionException e) {
                // Smother because the value is probably somewhere else
            }
        }

        // Next, check the objects
        if (value == null && objects.containsKey(key)) {
            value = objects.get(key);
        }

        // Next, check the request
        if (value == null) {
            value = request.getAttribute(key);
        }

        // Next, check the session
        if (value == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                value = session.getAttribute(key);
            }
        }

        // Next, check the context
        if (value == null) {
            value = context.getAttribute(key);
        }

        try {
            return ObjectWrapper.DEFAULT_WRAPPER.wrap(value);
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
    }

    public TemplateCollectionModel keys() {
        Set<String> keys = append(objects.keySet(), iterable(request.getAttributeNames()),
            iterable(request.getSession().getAttributeNames()), iterable(context.getAttributeNames()));
        if (action != null) {
            keys.addAll(expressionEvaluator.getAllMembers(action.getClass()));
        }

        keys.add(JSP_TAGLIBS);

        return new SimpleCollection(keys);
    }

    public TemplateCollectionModel values() {
        Collection<Object> values = new ArrayList<Object>(objects.values());
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

        values.add(taglibFactory);

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