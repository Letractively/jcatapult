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

import java.lang.annotation.Annotation;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.ResultAnnotation;
import org.jcatapult.mvc.action.result.annotation.ResultContainerAnnotation;

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
    private final ForwardResult forwardResult;

    @Inject
    public DefaultResultInvocationProvider(ForwardResult forwardResult) {
        this.forwardResult = forwardResult;
    }

    /**
     * <p>
     * Delegates to the {@link #defaultResult(ActionInvocation)} method.
     * </p>
     *
     * @param   invocation The current action invocation.
     * @return  The result invocation that is a forward or redirect, depending on the situation.
     *          Or null if there isn't a forwardable resource in the web application for the given URI.
     */
    public ResultInvocation lookup(final ActionInvocation invocation) {
        Annotation annotation = defaultResult(invocation);
        if (annotation == null) {
            return null;
        }

        return new DefaultResultInvocation(annotation, invocation.actionURI(), null);
    }

    /**
     * <p>
     * Checks for results using this search order:
     * </p>
     *
     * <ol>
     * <li>Action annotations that are {@link ResultAnnotation}s, have a code method whose return
     *  value matches the result code</li>
     * <li>Delegates to the {@link #defaultResult(ActionInvocation, String)} method. </li>
     * </ol>
     *
     * @param   invocation The action invocation used to look for annotations.
     * @param   resultCode The result code from the action invocation.
     * @return  The result invocation from the annotation or a forward based on any pages that
     *          were found.
     */
    public ResultInvocation lookup(ActionInvocation invocation, String resultCode) {
        String uri = invocation.actionURI();
        Object action = invocation.action();
        Annotation[] annotations = action.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(ResultAnnotation.class)) {
                if (matchesCode(resultCode, annotation)) {
                    return new DefaultResultInvocation(annotation, uri, resultCode);
                }
            } else if (annotation.annotationType().isAnnotationPresent(ResultContainerAnnotation.class)) {
                // There are multiple annotations inside the value
                try {
                    Annotation[] results = (Annotation[]) invokeMethod("value", annotation);
                    for (Annotation result : results) {
                        if (matchesCode(resultCode, result)) {
                            return new DefaultResultInvocation(result, uri, resultCode);
                        }
                    }
                } catch (ReflectionException e) {
                    throw new RuntimeException("Custom result annotation containers must have a method " +
                        "named [value] that is an array of result annotations.");
                }
            }

        }

        Annotation annotation = defaultResult(invocation, resultCode);
        return new DefaultResultInvocation(annotation, uri, resultCode);
    }


    /**
     * Locates the default Forward for an action invocation. This method is only used for action-less
     * invocations.
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
     * @param   invocation The action invocation.
     * @return  The Forward and or null if it doesn't exist.
     */
    private Annotation defaultResult(final ActionInvocation invocation) {
        String uri = invocation.actionURI();
        String extension = invocation.extension();
        Forward forward = null;
        if (uri.endsWith("/")) {
            forward = forwardResult.findResult(uri + "index.jsp", null);
            if (forward == null) {
                forward = forwardResult.findResult(uri + "index.ftl", null);
            }
        } else {
            // Check for the simple forward
            if (extension != null) {
                forward = forwardResult.findResult(uri + "-" + extension + ".jsp", null);
                if (forward == null) {
                    forward = forwardResult.findResult(uri + "-" + extension + ".ftl", null);
                }
            }

            if (forward == null) {
                forward = forwardResult.findResult(uri + ".jsp", null);
            }
            if (forward == null) {
                forward = forwardResult.findResult(uri + ".ftl", null);
            }
            if (forward == null) {
                forward = forwardResult.findResult(uri + "/index.jsp", null);
                if (forward == null) {
                    forward = forwardResult.findResult(uri + "/index.ftl", null);
                }

                if (forward != null) {
                    return new RedirectResult.RedirectImpl(uri + "/", null, false);
                }
            }
        }

        return forward;
    }

    /**
     * Locates the default Forward for an action invocation and result code from an action.
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
     * @param   invocation The action invocation.
     * @param   resultCode The result code from the action invocation.
     * @return  The Forward and never null.
     * @throws  RuntimeException If the default forward could not be found.
     */
    private Annotation defaultResult(ActionInvocation invocation, String resultCode) {
        String uri = invocation.actionURI();
        String extension = invocation.extension();
        Forward forward = null;
        if (uri.endsWith("/")) {
            forward = forwardResult.findResult(uri + "index.jsp", resultCode);
            if (forward == null) {
                forward = forwardResult.findResult(uri + "index.ftl", resultCode);
            }
        } else {
            if (extension != null) {
                forward = forwardResult.findResult(uri + "-" + extension + "-" + resultCode + ".jsp", null);
                if (forward == null) {
                    forward = forwardResult.findResult(uri + "-" + extension + "-" + resultCode + ".ftl", null);
                }
                if (forward == null) {
                    forward = forwardResult.findResult(uri + "-" + extension + ".jsp", null);
                }
                if (forward == null) {
                    forward = forwardResult.findResult(uri + "-" + extension + ".ftl", null);
                }
            }

            // Look for JSP and FTL results to forward to
            if (forward == null) {
                forward = forwardResult.findResult(uri + "-" + resultCode + ".jsp", resultCode);
            }
            if (forward == null) {
                forward = forwardResult.findResult(uri + "-" + resultCode + ".ftl", resultCode);
            }
            if (forward == null) {
                forward = forwardResult.findResult(uri + ".jsp", resultCode);
            }
            if (forward == null) {
                forward = forwardResult.findResult(uri + ".ftl", resultCode);
            }
        }

        if (forward == null) {
            throw new RuntimeException("Unable to locate result for URI [" + invocation.uri() +
                "] and result code [" + resultCode + "]");
        }

        return forward;
    }

    private boolean matchesCode(String resultCode, Annotation annotation) {
        try {
            String code = (String) invokeMethod("code", annotation);
            if (code.equals(resultCode)) {
                return true;
            }
        } catch (ReflectionException e) {
            throw new RuntimeException("Custom result annotations must have a method named " +
                "[code] that contains the result code they are associated with.");
        }

        return false;
    }
}