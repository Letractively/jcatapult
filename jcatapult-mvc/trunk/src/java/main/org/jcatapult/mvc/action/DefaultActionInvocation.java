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
package org.jcatapult.mvc.action;

import org.jcatapult.mvc.action.config.ActionConfiguration;

/**
 * <p>
 * This class is the default action invocation implementation.
 * It provides a simple immutable Struct for containing the
 * values of the invocation.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultActionInvocation implements ActionInvocation {
    private final Object action;
    private final String uri;
    private final ActionConfiguration configuration;

    public DefaultActionInvocation(Object action, String uri, ActionConfiguration configuration) {
        this.action = action;
        this.uri = uri;
        this.configuration = configuration;
    }

    public Object action() {
        return action;
    }

    public String actionURI() {
        return uri;
    }

    public ActionConfiguration configuration() {
        return configuration;
    }
}