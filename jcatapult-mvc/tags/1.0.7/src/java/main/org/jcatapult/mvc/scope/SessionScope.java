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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jcatapult.mvc.scope.annotation.Session;

import com.google.inject.Inject;

/**
 * <p>
 * This is the request scope which fetches and stores values in the
 * HttpSession.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class SessionScope implements Scope<Session> {
    private final HttpSession session;

    @Inject
    public SessionScope(HttpServletRequest request) {
        this.session = request.getSession();
    }

    /**
     * {@inheritDoc}
     */
    public Object get(String fieldName, Session scope) {
        String key = scope.value().equals("##field-name##") ? fieldName : scope.value();
        return session.getAttribute(key);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String fieldName, Object value, Session scope) {
        String key = scope.value().equals("##field-name##") ? fieldName : scope.value();
        session.setAttribute(key, value);
    }
}