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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.scope.annotation.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This is the request scope which fetches and stores values in the
 * ServletContext.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Singleton
public class ContextScope implements Scope<Context> {
    private final ServletContext context;

    @Inject
    public ContextScope(ServletContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object action, String fieldName, HttpServletRequest request) {
        return context.getAttribute(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    public void set(Object action, String fieldName, HttpServletRequest request, Object value) {
        context.setAttribute(fieldName, value);
    }

    /**
     * {@inheritDoc}
     */
    public Class<Context> annotationType() {
        return Context.class;
    }
}