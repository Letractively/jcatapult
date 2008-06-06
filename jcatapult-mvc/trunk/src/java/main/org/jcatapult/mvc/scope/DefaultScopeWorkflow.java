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
package org.jcatapult.mvc.scope;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.parameters.el.ExpressionEvaluator;
import org.jcatapult.mvc.scope.annotation.ScopeAnnotation;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This class handles all of the scopes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultScopeWorkflow implements ScopeWorkflow {
    private final FlashScope flashScope;
    private final ScopeRegistry scopeRegistry;
    private final ActionMappingWorkflow actionMappingWorkflow;
    private final ExpressionEvaluator expressionEvaluator;

    @Inject
    public DefaultScopeWorkflow(ActionMappingWorkflow actionMappingWorkflow,
            ExpressionEvaluator expressionEvaluator, FlashScope flashScope,
            ScopeRegistry scopeRegistry) {
        this.actionMappingWorkflow = actionMappingWorkflow;
        this.expressionEvaluator = expressionEvaluator;
        this.flashScope = flashScope;
        this.scopeRegistry = scopeRegistry;
    }

    /**
     * Handles the incoming HTTP request for scope values.
     *
     * @param   request The request.
     * @param   response The response.
     * @param   chain The workflow chain.
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        handleFlashScope(request);

        ActionInvocation actionInvocation = actionMappingWorkflow.fetch(request);
        Object action = actionInvocation.action();

        // Handle loading scoped members into the action
        if (action != null) {
            loadScopedMembers(action, request);
        }

        chain.doWorkflow(request, response);

        // Handle storing scoped members from the action
        if (action != null) {
            storeScopedMembers(action, request);
        }
    }

    /**
     * Handles trasnfer of the flash scope by calling the {@link org.jcatapult.mvc.scope.FlashScope#transferFlash(javax.servlet.http.HttpServletRequest)}
     * method.
     *
     * @param   request The request used for the transfer.
     */
    protected void handleFlashScope(HttpServletRequest request) {
        // Transfer the flash
        flashScope.transferFlash(request);
    }

    /**
     * Loads all of the values into the action from the scopes.
     *
     * @param   action The action to sets the values from scopes into.
     * @param   request The request.
     */
    protected void loadScopedMembers(Object action, HttpServletRequest request) {
        Class<?> klass = action.getClass();
        Field[] fields = klass.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> type = annotation.annotationType();
                String fieldName = field.getName();

                if (type.isAnnotationPresent(ScopeAnnotation.class)) {
                    Scope scope = scopeRegistry.lookup(type);
                    Object value = scope.get(action, fieldName, request);
                    if (value != null) {
                        expressionEvaluator.setValue(fieldName, action, value);
                    }
                }
            }
        }
    }

    /**
     * Stores all of the values from the action from into the scopes.
     *
     * @param   action The action to get the values from.
     * @param   request The request.
     */
    protected void storeScopedMembers(Object action, HttpServletRequest request) {
        Class<?> klass = action.getClass();
        Field[] fields = klass.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> type = annotation.annotationType();
                String fieldName = field.getName();

                if (type.isAnnotationPresent(ScopeAnnotation.class)) {
                    Scope scope = scopeRegistry.lookup(type);
                    Object value = expressionEvaluator.getValue(fieldName, action);
                    scope.set(action, fieldName, request, value);
                }
            }
        }
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }
}