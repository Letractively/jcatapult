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
 *
 */
package org.jcatapult.mvc.action.result;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import javax.servlet.ServletContext;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.ResultAnnotation;

import com.google.inject.Inject;
import net.java.lang.reflect.ReflectionException;
import static net.java.lang.reflect.ReflectionTools.*;

/**
 * <p>
 * This class is the default implementation of the result provider.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultResultInvocationProvider implements ResultInvocationProvider {
    public static final String DIR = "/WEB-INF/content";
    private final ServletContext servletContext;

    @Inject
    public DefaultResultInvocationProvider(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * <p>
     * Checks for results using this search order:
     * </p>
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
     * @param   uri The URI to append to the page.
     * @return  The result invocation that is a forward.
     */
    public ResultInvocation lookup(final String uri) {
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

        if (forward == null) {
            throw new RuntimeException("Unable to locate default result for URI [" + uri + "]");
        }

        return new DefaultResultInvocation(forward, uri, null);
    }

    /**
     * <p>
     * Checks for results using this search order:
     * </p>
     *
     * <ol>
     * <li>Action annotations that are {@link ResultAnnotation}s, have a code method whose return
     *  value matches the result code</li>
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
     * @param   invocation The action invocation used to look for annotations.
     * @param   uri The URI to append to the page.
     * @param   resultCode The result code from the action invocation.
     * @return  The result invocation from the annotation or a forward based on any pages that
     *          were found.
     */
    public ResultInvocation lookup(ActionInvocation invocation, String uri, String resultCode) {
        Object action = invocation.action();
        Annotation[] annotations = action.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(ResultAnnotation.class)) {
                try {
                    String code = (String) invokeMethod("code", annotation);
                    if (code.equals(resultCode)) {
                        return new DefaultResultInvocation(annotation, uri, resultCode);
                    }
                } catch (ReflectionException e) {
                    throw new RuntimeException("Custom result annotations must have a method named " +
                        "[code] that contains the result code they are associated with.");
                }
            }
        }

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

        return new DefaultResultInvocation(forward, uri, resultCode);
    }

    protected Forward findResult(String path, String resultCode) {
        try {
            String classLoaderPath = path.startsWith("/") ? path.substring(1, path.length()) : path;
            if (servletContext.getResource(path) != null || getClass().getResource(classLoaderPath) != null) {
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