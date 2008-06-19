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

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.scope.annotation.ScopeAnnotation;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles all of the scopes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultScopeWorkflow implements ScopeWorkflow {
    private final ActionInvocationStore actionInvocationStore;
    private final ExpressionEvaluator expressionEvaluator;
    private final FlashScope flashScope;
    private final ScopeProvider scopeProvider;

    @Inject
    public DefaultScopeWorkflow(ActionInvocationStore actionInvocationStore,
            ExpressionEvaluator expressionEvaluator, FlashScope flashScope, ScopeProvider scopeProvider) {
        this.actionInvocationStore = actionInvocationStore;
        this.expressionEvaluator = expressionEvaluator;
        this.flashScope = flashScope;
        this.scopeProvider = scopeProvider;
    }

    /**
     * Handles the incoming HTTP request for scope values.
     *
     * @param   chain The workflow chain.
     */
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        flashScope.transferFlash();

        ActionInvocation actionInvocation = actionInvocationStore.get();
        Object action = actionInvocation.action();

        // Handle loading scoped members into the action
        if (action != null) {
            loadScopedMembers(action);
        }

        chain.continueWorkflow();

        // Handle storing scoped members from the action
        if (action != null) {
            storeScopedMembers(action);
        }
    }

    /**
     * Loads all of the values into the action from the scopes.
     *
     * @param   action The action to sets the values from scopes into.
     */
    protected void loadScopedMembers(Object action) {
        Class<?> klass = action.getClass();
        Field[] fields = klass.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> type = annotation.annotationType();
                String fieldName = field.getName();

                if (type.isAnnotationPresent(ScopeAnnotation.class)) {
                    Scope scope = scopeProvider.lookup(type);
                    Object value = scope.get(fieldName);
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
     */
    protected void storeScopedMembers(Object action) {
        Class<?> klass = action.getClass();
        Field[] fields = klass.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> type = annotation.annotationType();
                String fieldName = field.getName();

                if (type.isAnnotationPresent(ScopeAnnotation.class)) {
                    Scope scope = scopeProvider.lookup(type);
                    Object value = expressionEvaluator.getValue(fieldName, action);
                    scope.set(fieldName, value);
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