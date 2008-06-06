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
package org.jcatapult.mvc.parameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.messages.MessageStore;
import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.el.ExpressionEvaluator;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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
@Singleton
public class DefaultParameterWorkflow implements ParameterWorkflow {
    public static final String CHECKBOX_PREFIX = "__jc_cb";
    public static final String RADIOBUTTON_PREFIX = "__jc_rb";
    public static final String ACTION_PREFIX = "__jc_a";
    public static final String PARAMETERS_KEY = "__jcatapult_parameters_key";

    private final LocaleWorkflow localeWorkflow;
    private final ActionMappingWorkflow actionMappingWorkflow;
    private final MessageStore messageStore;
    private final ExpressionEvaluator expressionEvaluator;
    private boolean ignoreEmptyParameters = true;

    @Inject
    public DefaultParameterWorkflow(LocaleWorkflow localeWorkflow, ActionMappingWorkflow actionMappingWorkflow,
            MessageStore messageStore, ExpressionEvaluator expressionEvaluator) {
        this.localeWorkflow = localeWorkflow;
        this.actionMappingWorkflow = actionMappingWorkflow;
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
     * @param   request The request.
     * @param   response The response.
     * @param   chain The workflow chain.
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        ActionInvocation actionInvocation = actionMappingWorkflow.fetch(request);
        Object action = actionInvocation.action();

        if (action != null) {
            // First grab the structs and then save them to the request
            Map<String, Struct> structs = getValuesToSet(request);
            request.setAttribute(PARAMETERS_KEY, structs);

            // Next, process them
            Locale locale = localeWorkflow.getLocale(request);
            for (String key : structs.keySet()) {
                Struct struct = structs.get(key);
                try {
                    expressionEvaluator.setValue(key, action, struct.values, request, response, locale, struct.attributes);
                } catch (ConversionException ce) {
                    messageStore.addConversionError(action.getClass().getName(), key, locale, struct.attributes, struct.values);
                }
            }
        }

        chain.doWorkflow(request, response);
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }

    /**
     * Pulls the attributes for the given parameter out of the request. The Structs that are setup
     * by the perform method are stored in the request under the key {@code __jcatapult_parameters_key}.
     * The Strut for the given parameter is then pulled out and the attributes from the Struct are
     * returned.
     *
     * @param   request The request.
     * @param   parameter The name of the parameter.
     * @return  The attributes or an empty Map if the parameter has no attributes.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> fetchAttributes(HttpServletRequest request, String parameter) {
        Map<String, Struct> structs = (Map<String, Struct>) request.getAttribute(PARAMETERS_KEY);
        if (structs == null) {
            throw new AssertionError("The parameters structs are missing from the request.");
        }

        Struct struct = structs.get(parameter);
        return struct == null ? new HashMap<String, String>() : struct.attributes;
    }

    /**
     * Cleanses the HTTP request parameters by removing all the special JCatapult MVC marker parameters
     * for checkboxes, radio buttons and actions. It also adds into the parameters null values for
     * any un-checked checkboxes and un-selected radio buttons. It also collects all of the attributes
     * for each parameter using the {@code @} delimiter character.
     *
     * @param   request The request.
     * @return  The parameters to set into the aciton.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Struct> getValuesToSet(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();

        // Pull out the check box, radio button and action parameters
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

        // Remove actions from the parameters as they should be ignored right now
        for (String actionKey : actions) {
            structs.remove(actionKey);
        }

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