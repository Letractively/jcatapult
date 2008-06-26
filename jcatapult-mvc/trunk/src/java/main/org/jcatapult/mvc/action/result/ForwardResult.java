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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.locale.annotation.CurrentLocale;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.result.control.Control;

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
    private static final Map<String, Class<? extends Control>> controls = new HashMap<String, Class<? extends Control>>();
    private final Locale locale;
    private final ServletContext servletContext;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final FreeMarkerService freeMarkerService;
    private final ObjectFactory objectFactory;

    /**
     * Initializes the list of control classes.
     *
     * @param   objectFactory The object factory.
     */
    @Inject
    public static void initialize(ObjectFactory objectFactory) {
        List<Class<? extends Control>> types = objectFactory.getAllForType(Control.class);
        for (Class<? extends Control> type : types) {
            controls.put(type.getSimpleName().toLowerCase(), type);
        }
    }

    @Inject
    public ForwardResult(@CurrentLocale Locale locale, ServletContext servletContext,
            HttpServletRequest request, HttpServletResponse response,
            ExpressionEvaluator expressionEvaluator, FreeMarkerService freeMarkerService,
            ObjectFactory objectFactory) {
        super(expressionEvaluator);
        this.locale = locale;
        this.servletContext = servletContext;
        this.request = request;
        this.response = response;
        this.freeMarkerService = freeMarkerService;
        this.objectFactory = objectFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Forward forward, ActionInvocation invocation) throws IOException, ServletException {
        String page = forward.page();
        if (!page.startsWith("/")) {
            page = DIR + invocation.actionURI() + "/" + page;
        }

        if (page.endsWith(".jsp")) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(page);
            requestDispatcher.forward(wrapRequest(invocation, request), response);
        } else if (page.endsWith(".ftl")) {
            PrintWriter writer = response.getWriter();
            FreeMarkerMap map = new FreeMarkerMap(servletContext, request, expressionEvaluator,
                invocation.action(), controls, objectFactory);
            freeMarkerService.render(writer, page, map, locale);
        }
    }

    /**
     * Locates the default Forward for a URI. This method is only used for action-less invocations.
     *
     * <ol>
     * <li>/WEB-INF/content/&lt;uri>.jsp</li>
     * <li>/WEB-INF/content/&lt;uri>.ftl</li>
     * <li>/WEB-INF/content/&lt;uri>/index.jsp</li>
     * <li>/WEB-INF/content/&lt;uri>/index.ftl</li>
     * </ol>
     *
     * <p>
     * If nothing is found this bombs out.
     * </p>
     *
     * @param   uri The URI.
     * @return  The Forward and or null if it doesn't exist.
     */
    public Forward defaultForward(final String uri) {
        // This is always a forward
        Forward forward = findResult(DIR + uri + ".jsp", null);
        if (forward == null) {
            forward = findResult(DIR + uri + ".ftl", null);
        }
        if (forward == null) {
            forward = findResult(DIR + uri + "/index.jsp", null);
        }
        if (forward == null) {
            forward = findResult(DIR + uri + "/index.ftl", null);
        }

        return forward;
    }

    /**
     * Locates the default Forward for a URI and result code from an action.
     *
     * <p>
     * Checks for results using this search order:
     * </p>
     *
     * <ol>
     * <li>/WEB-INF/content/&lt;uri>-&lt;resultCode>.jsp</li>
     * <li>/WEB-INF/content/&lt;uri>-&lt;resultCode>.ftl</li>
     * <li>/WEB-INF/content/&lt;uri>.jsp</li>
     * <li>/WEB-INF/content/&lt;uri>.ftl</li>
     * <li>/WEB-INF/content/&lt;uri>/index.jsp</li>
     * <li>/WEB-INF/content/&lt;uri>/index.ftl</li>
     * </ol>
     *
     * <p>
     * If nothing is found this bombs out.
     * </p>
     *
     * @param   uri The URI.
     * @param   resultCode The result code from the action invocation.
     * @return  The Forward and never null.
     * @throws  RuntimeException If the default forward could not be found.
     */
    public Forward defaultForward(String uri, String resultCode) {
        // Look for JSP and FTL results to forward to
        Forward forward = findResult(DIR + uri + "-" + resultCode + ".jsp", resultCode);
        if (forward == null) {
            forward = findResult(DIR + uri + "-" + resultCode + ".ftl", resultCode);
        }
        if (forward == null) {
            forward = findResult(DIR + uri + ".jsp", resultCode);
        }
        if (forward == null) {
            forward = findResult(DIR + uri + ".ftl", resultCode);
        }
        if (forward == null) {
            forward = findResult(DIR + uri + "/index.jsp", resultCode);
        }
        if (forward == null) {
            forward = findResult(DIR + uri + "/index.ftl", resultCode);
        }

        if (forward == null) {
            throw new RuntimeException("Unable to locate result for URI [" + uri + "] and result code [" +
                resultCode + "]");
        }

        return forward;
    }

    protected Forward findResult(String path, String resultCode) {
        try {
            String classLoaderPath = path.startsWith("/") ? path.substring(1, path.length()) : path;
            if (servletContext.getResource(path) != null ||
                    Thread.currentThread().getContextClassLoader().getResource(classLoaderPath) != null) {
                return new ForwardImpl(path, resultCode);
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