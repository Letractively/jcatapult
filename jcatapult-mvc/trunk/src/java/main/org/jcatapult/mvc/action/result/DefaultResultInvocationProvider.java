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
     * Delegates to the {@link ForwardResult#defaultForward(String)} method.
     * </p>
     *
     * @param   uri The URI to append to the page.
     * @return  The result invocation that is a forward or null if there isn't a forwardable resource
     *          in the web application for the given URI.
     */
    public ResultInvocation lookup(final String uri) {
        // This is always a forward
        Forward forward = forwardResult.defaultForward(uri);
        if (forward == null) {
            return null;
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
     * <li>Delegates to the {@link ForwardResult#defaultForward(String, String)} method. </li>
     * </ol>
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

        Forward forward = forwardResult.defaultForward(uri, resultCode);
        return new DefaultResultInvocation(forward, uri, resultCode);
    }
}