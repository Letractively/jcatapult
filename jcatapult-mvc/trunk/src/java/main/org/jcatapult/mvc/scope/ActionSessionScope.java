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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.scope.annotation.ActionSession;

/**
 * <p>
 * This is the request scope which fetches and stores values in the
 * HttpSession, but those values are associated with a specific action.
 * In order to accomplish this, a Map is put into the session under the
 * key <strong>JCATAPULT_ACTION_SESSION</code> and the values are stored
 * inside that Map. The key is the name of the action class and the value
 * is a Map. The second Map's key is the fieldName and the value is the
 * value being stored.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class ActionSessionScope implements Scope<ActionSession> {
    public static final String ACTION_SESSION_KEY = "JCATAPULT_ACTION_SESSION_SCOPE";

    /**
     * {@inheritDoc}
     */
    public Object get(Object action, String fieldName, HttpServletRequest request) {
        Map<String, Map<String, Object>> actionSession = (Map<String, Map<String, Object>>) request.getSession().getAttribute(ACTION_SESSION_KEY);
        if (actionSession == null) {
            return null;
        }

        Map<String, Object> values = actionSession.get(fieldName);
        if (values == null) {
            return null;
        }

        return values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    public void set(Object action, String fieldName, HttpServletRequest request, Object value) {
        Map<String, Map<String, Object>> actionSession = (Map<String, Map<String, Object>>) request.getSession().getAttribute(ACTION_SESSION_KEY);
        if (actionSession == null) {
            actionSession = new HashMap<String, Map<String, Object>>();
            request.getSession().setAttribute(ACTION_SESSION_KEY, actionSession);
        }

        Map<String, Object> values = actionSession.get(fieldName);
        if (values == null) {
            values = new HashMap<String, Object>();
            actionSession.put(action.getClass().getName(), values);
        }

        values.put(fieldName, value);
    }

    /**
     * {@inheritDoc}
     */
    public Class<ActionSession> annotationType() {
        return ActionSession.class;
    }
}