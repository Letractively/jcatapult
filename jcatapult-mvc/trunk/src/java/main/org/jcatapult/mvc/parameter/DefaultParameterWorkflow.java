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
package org.jcatapult.mvc.parameter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * <p>
 * This class uses the {@link ExpressionEvaluator} to process the incoming
 * request parameters. It also handles check boxes, submit buttons and radio
 * buttons.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultParameterWorkflow implements ParameterWorkflow {
    public static final String CHECKBOX_PREFIX = "__jc_cb_";
    public static final String RADIOBUTTON_PREFIX = "__jc_rb_";
    public static final String ACTION_PREFIX = "__jc_a_";

    private final HttpServletRequest request;
    private final ActionInvocationStore actionInvocationStore;
    private final MessageStore messageStore;
    private final ExpressionEvaluator expressionEvaluator;
    private boolean ignoreEmptyParameters = true;

    @Inject
    public DefaultParameterWorkflow(HttpServletRequest request, ActionInvocationStore actionInvocationStore,
            MessageStore messageStore, ExpressionEvaluator expressionEvaluator) {
        this.request = request;
        this.actionInvocationStore = actionInvocationStore;
        this.messageStore = messageStore;
        this.expressionEvaluator = expressionEvaluator;
    }

    @Inject(optional = true)
    public void setIgnoreEmptyParamaters(@Named("jcatapult.mvc.ignoreEmptyParameters") boolean ignoreEmptyParameters) {
        this.ignoreEmptyParameters = ignoreEmptyParameters;
    }

    /**
     * Handles the incoming HTTP request parameters.
     *
     * @param   chain The workflow chain.
     */
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        ActionInvocation actionInvocation = actionInvocationStore.get();
        Object action = actionInvocation.action();

        if (action != null) {
            // First grab the structs and then save them to the request
            Map<String, Struct> structs = getValuesToSet(request);

            // Next, process them
            for (String key : structs.keySet()) {
                Struct struct = structs.get(key);
                try {
                    expressionEvaluator.setValue(key, action, struct.values, struct.attributes);
                } catch (ConversionException ce) {
                    messageStore.addConversionError(key, action.getClass().getName(), struct.attributes, (Object[]) struct.values);
                }
            }
        }

        chain.continueWorkflow();
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }

    /**
     * Cleanses the HTTP request parameters by removing all the special JCatapult MVC marker parameters
     * for checkboxes, radio buttons and actions. It also adds into the parameters null values for
     * any un-checked checkboxes and un-selected radio buttons. It also collects all of the dynamic
     * attributes for each parameter using the {@code @} delimiter character.
     *
     * @param   request The request.
     * @return  The parameters to set into the aciton.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Struct> getValuesToSet(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();

        // Pull out the check box, radio button and action parameter
        Set<String> checkBoxes = new HashSet<String>();
        Set<String> radioButtons = new HashSet<String>();
        Set<String> actions = new HashSet<String>();
        Map<String, Struct> structs = new HashMap<String, Struct>();
        for (String key : parameters.keySet()) {
            if (key.startsWith(CHECKBOX_PREFIX)) {
                checkBoxes.add(key.substring(CHECKBOX_PREFIX.length()));
            } else if (key.startsWith(RADIOBUTTON_PREFIX)) {
                radioButtons.add(key.substring(RADIOBUTTON_PREFIX.length()));
            } else if (key.startsWith(ACTION_PREFIX)) {
                actions.add(key.substring(ACTION_PREFIX.length()));
            } else {
                int index = key.indexOf("@");
                String parameter = (index > 0) ? key.substring(0, index) : key;
                Struct s = structs.get(parameter);
                if (s == null) {
                    s = new Struct();
                    structs.put(parameter, s);
                }

                if (index > 0) {
                    s.attributes.put(key.substring(index + 1), parameters.get(key)[0]);
                } else {
                    // Only add the values if there is something in them. Otherwise, they are worthless
                    // empty text fields.
                    String[] values = parameters.get(parameter);
                    if (!ignoreEmptyParameters || !empty(values)) {
                        s.values = values;
                    }
                }
            }
        }

        // Remove all the existing checkbox, radio and action keys
        checkBoxes.removeAll(structs.keySet());
        radioButtons.removeAll(structs.keySet());

        // Add back in any left overs as null values
        for (String checkBox : checkBoxes) {
            structs.put(checkBox, new Struct());
        }

        for (String radioButton : radioButtons) {
            structs.put(radioButton, new Struct());
        }

        // Remove actions from the parameter as they should be ignored right now
        structs.keySet().removeAll(actions);

        return structs;
    }

    private boolean empty(String[] values) {
        if (values != null && values.length > 0) {
            for (String value : values) {
                if (!value.equals("")) {
                    return false;
                }
            }
        }

        return true;
    }

    private class Struct {
        Map<String, String> attributes = new HashMap<String, String>();
        String[] values;
    }
}