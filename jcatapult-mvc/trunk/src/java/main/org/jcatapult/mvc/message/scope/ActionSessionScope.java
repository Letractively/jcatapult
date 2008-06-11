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
package org.jcatapult.mvc.message.scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This is the message scope which fetches and stores messages in the
 * HttpSession, but are associated with a specific action. In order to
 * accomplish this, a Map is put into the session under various different
 * keys (one key for each type of message - i.e. field errors, action
 * messages, etc.). The key of this Map is the name of the action class
 * and the value is the location where the messages are stored, either a
 * Map or a List depending on the type of message.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class ActionSessionScope extends AbstractScope {
    /**
     * The location where the field errors are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the field errors Map.
     */
    public static final String ACTION_SESSION_FIELD_ERROR_KEY = "jcatapultActionSessionFieldErrors";

    /**
     * The location where the field messages are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the field messages Map.
     */
    public static final String ACTION_SESSION_FIELD_MESSAGE_KEY = "jcatapultActionSessionFieldMessages";

    /**
     * The location where the action errors are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the action errors List.
     */
    public static final String ACTION_SESSION_ACTION_ERROR_KEY = "jcatapultActionSessionActionErrors";

    /**
     * The location where the action messages are stored. This keys into the session a Map whose key
     * is the action's class name and whose value is the action messages List.
     */
    public static final String ACTION_SESSION_ACTION_MESSAGE_KEY = "jcatapultActionSessionActionMessages";

    /**
     * {@inheritDoc}
     */
    protected Map<String, List<String>> getFieldScope(HttpServletRequest request, MessageType type, Object action) {
        String key = (type == MessageType.ERROR) ? ACTION_SESSION_FIELD_ERROR_KEY : ACTION_SESSION_FIELD_MESSAGE_KEY;
        Map<String, Map<String, List<String>>> actionSession = (Map<String, Map<String, List<String>>>) request.getSession().getAttribute(key);
        if (actionSession == null) {
            actionSession = new HashMap<String, Map<String, List<String>>>();
            request.getSession().setAttribute(key, actionSession);
        }

        String className = action.getClass().getName();
        Map<String, List<String>> values = actionSession.get(className);
        if (values == null) {
            values = new HashMap<String, List<String>>();
            actionSession.put(className, values);
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    protected List<String> getActionScope(HttpServletRequest request, MessageType type, Object action) {
        String key = (type == MessageType.ERROR) ? ACTION_SESSION_ACTION_ERROR_KEY : ACTION_SESSION_ACTION_MESSAGE_KEY;
        Map<String, List<String>> actionSession = (Map<String, List<String>>) request.getSession().getAttribute(key);
        if (actionSession == null) {
            actionSession = new HashMap<String, List<String>>();
            request.getSession().setAttribute(key, actionSession);
        }

        String className = action.getClass().getName();
        List<String> values = actionSession.get(className);
        if (values == null) {
            values = new ArrayList<String>();
            actionSession.put(className, values);
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public MessageScope scope() {
        return MessageScope.ACTION_SESSION;
    }
}