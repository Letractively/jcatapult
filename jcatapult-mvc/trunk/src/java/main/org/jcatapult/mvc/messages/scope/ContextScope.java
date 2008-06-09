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
package org.jcatapult.mvc.messages.scope;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This is the message scope which fetches and stores values in the
 * ServletContext. The values are stored in the servlet context using
 * a variety of different keys.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Singleton
public class ContextScope extends AbstractJEEScope {
    private final ServletContext context;

    @Inject
    public ContextScope(ServletContext context) {
        this.context = context;
    }

    /**
     * Looks up a value from the servlet context.
     *
     * @param   request Not used.
     * @param   key The key to lookup the value from.
     * @return  The value or null if it doesn't exist.
     */
    protected Object findScope(HttpServletRequest request, String key) {
        return context.getAttribute(key);
    }

    /**
     * Stores a value into the servlet context.
     *
     * @param   request Not used.
     * @param   key The key to store the value under.
     * @param   scope The value to store.
     */
    protected void storeScope(HttpServletRequest request, String key, Object scope) {
        context.setAttribute(key, scope);
    }

    /**
     * {@inheritDoc}
     */
    public MessageScope scope() {
        return MessageScope.CONTEXT;
    }
}