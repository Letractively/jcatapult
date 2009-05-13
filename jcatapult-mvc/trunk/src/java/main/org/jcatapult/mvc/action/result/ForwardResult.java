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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.locale.annotation.CurrentLocale;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.freemarker.FreeMarkerMap;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * <p>
 * This result performs a servlet forward to a JSP or renders a FreeMarker
 * template depending on the extension of the page.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ForwardResult extends AbstractResult<Forward> {
    public static final String DIR = "/WEB-INF/content";
    private final Locale locale;
    private final ServletContext servletContext;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final FreeMarkerService freeMarkerService;
    private final ActionInvocationStore actionInvocationStore;

    @Inject
    public ForwardResult(@CurrentLocale Locale locale, ServletContext servletContext,
                         HttpServletRequest request, HttpServletResponse response,
                         ExpressionEvaluator expressionEvaluator, FreeMarkerService freeMarkerService,
                         ActionInvocationStore actionInvocationStore) {
        super(expressionEvaluator);
        this.locale = locale;
        this.servletContext = servletContext;
        this.request = request;
        this.response = response;
        this.freeMarkerService = freeMarkerService;
        this.actionInvocationStore = actionInvocationStore;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Forward forward, ActionInvocation invocation) throws IOException, ServletException {
        // Set the default content type for the response. This also activates SiteMesh
        response.setContentType("text/html; charset=UTF-8");

        String page = expand(forward.page(), invocation.action());
        if (!page.startsWith("/")) {
            // Strip off the last part of the URI since it is relative
            String uri = invocation.actionURI();
            int index = uri.lastIndexOf("/");
            if (index >= 0) {
                uri = uri.substring(0, index);
            }

            page = DIR + uri  + "/" + page;
        }

        if (page.endsWith(".jsp")) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(page);
            requestDispatcher.forward(wrapRequest(invocation, request), response);
        } else if (page.endsWith(".ftl")) {
            PrintWriter writer = response.getWriter();
            FreeMarkerMap map = new FreeMarkerMap(request, response, expressionEvaluator, actionInvocationStore, new HashMap<String, Object>());
            freeMarkerService.render(writer, page, map, locale);
        }
    }

    protected Forward findResult(String path, String resultCode) {
        try {
            String fullPath = DIR + path;
            String classLoaderPath = fullPath.substring(1, fullPath.length());
            if (servletContext.getResource(fullPath) != null ||
                    Thread.currentThread().getContextClassLoader().getResource(classLoaderPath) != null) {
                return new ForwardResult.ForwardImpl(fullPath, resultCode);
            }
        } catch (MalformedURLException e) {
        }

        return null;
    }

    public static class ForwardImpl implements Forward {
        private final String uri;
        private final String code;

        public ForwardImpl(String uri, String code) {
            this.uri = uri;
            this.code = code;
        }

        public String code() {
            return code;
        }

        public String page() {
            return  uri;
        }

        public Class<? extends Annotation> annotationType() {
            return Forward.class;
        }
    }
}
