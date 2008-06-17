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
package org.jcatapult.mvc.action;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the default action invocation store.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultActionInvocationStore implements ActionInvocationStore {
    public static final String ACTION_INVOCATION_KEY = "jcatapultActionInvocation";
    private final HttpServletRequest request;

    @Inject
    public DefaultActionInvocationStore(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public ActionInvocation get() {
        return (ActionInvocation) request.getAttribute(ACTION_INVOCATION_KEY);
    }

    /**
     * {@inheritDoc}
     */
    public void set(ActionInvocation invocation) {
        request.setAttribute(ACTION_INVOCATION_KEY, invocation);
    }
}