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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.annotation.PostParameterMethod;
import org.jcatapult.mvc.parameter.annotation.PreParameter;
import org.jcatapult.mvc.parameter.annotation.PreParameterMethod;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.el.ExpressionException;
import org.jcatapult.mvc.util.MethodTools;
import org.jcatapult.mvc.util.RequestTools;
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

    @Inject public Logger logger;
    private final HttpServletRequest request;
    private final ActionInvocationStore actionInvocationStore;
    private final MessageStore messageStore;
    private final ExpressionEvaluator expressionEvaluator;
    private final EnvironmentResolver resolver;
    private boolean ignoreEmptyParameters = false;

    @Inject
    public DefaultParameterWorkflow(HttpServletRequest request, ActionInvocationStore actionInvocationStore,
            MessageStore messageStore, ExpressionEvaluator expressionEvaluator, EnvironmentResolver resolver) {
        this.request = request;
        this.actionInvocationStore = actionInvocationStore;
        this.messageStore = messageStore;
        this.expressionEvaluator = expressionEvaluator;
        this.resolver = resolver;
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
    @SuppressWarnings("unchecked")
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        ActionInvocation actionInvocation = actionInvocationStore.getCurrent();
        Object action = actionInvocation.action();

        if (action != null && RequestTools.canUseParameters(request)) {
            Map<String, String[]> parameters = request.getParameterMap();

            // First grab the structs
            Parameters params = getValuesToSet(parameters);

            // Next, handle pre parameters
            handlePreParameters(params, action, actionInvocation);

            // Next, invoke pre methods
            MethodTools.invokeAllWithAnnotation(action, PreParameterMethod.class);

            // Next, set the parameters
            handleParameters(params, action, actionInvocation);

            // Finally, invoke post methods
            MethodTools.invokeAllWithAnnotation(action, PostParameterMethod.class);
        } else if (action != null) {
            // TODO Handle setting the request body onto the action  
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
     * @param   parameters The request parameters.
     * @return  The parameters to set into the aciton.
     */
    protected Parameters getValuesToSet(Map<String, String[]> parameters) {
        Parameters result = new Parameters();

        // Pull out the check box, radio button and action parameter
        Map<String, String[]> checkBoxes = new HashMap<String, String[]>();
        Map<String, String[]> radioButtons = new HashMap<String, String[]>();
        Set<String> actions = new HashSet<String>();
        for (String key : parameters.keySet()) {
            if (InternalParameters.isInternalParameter(key)){
                continue;
            }

            boolean optional = (key.endsWith(".x") || key.endsWith(".y"));

            if (key.startsWith(CHECKBOX_PREFIX)) {
                checkBoxes.put(key.substring(CHECKBOX_PREFIX.length()), parameters.get(key));
            } else if (key.startsWith(RADIOBUTTON_PREFIX)) {
                radioButtons.put(key.substring(RADIOBUTTON_PREFIX.length()), parameters.get(key));
            } else if (key.startsWith(ACTION_PREFIX)) {
                actions.add(key.substring(ACTION_PREFIX.length()));
            } else {
                int index = key.indexOf("@");
                String parameter = (index > 0) ? key.substring(0, index) : key;

                Struct s;
                if (optional) {
                    s = result.optional.get(parameter);
                } else {
                    s = result.required.get(parameter);
                }

                if (s == null) {
                    s = new Struct();
                    if (optional) {
                        result.optional.put(parameter, s);
                    } else {
                        result.required.put(parameter, s);
                    }
                }

                if (index > 0) {
                    s.attributes.put(key.substring(index + 1), parameters.get(key)[0]);
                } else {
                    // If the ignore empty parameters flag is set, which IS NOT the default, this
                    // block will only ever add the values to the structure if they contain at least
                    // one non-empty String.
                    String[] values = parameters.get(parameter);
                    if (!ignoreEmptyParameters || !empty(values)) {
                        s.values = values;
                    }
                }
            }
        }

        // Remove all the existing checkbox, radio and action keys
        checkBoxes.keySet().removeAll(result.optional.keySet());
        checkBoxes.keySet().removeAll(result.required.keySet());
        radioButtons.keySet().removeAll(result.optional.keySet());
        radioButtons.keySet().removeAll(result.required.keySet());

        // Add back in any left overs
        addUncheckedValues(checkBoxes, result);
        addUncheckedValues(radioButtons, result);

        // Remove actions from the parameter as they should be ignored right now
        result.optional.keySet().removeAll(actions);
        result.required.keySet().removeAll(actions);

        return result;
    }

    /**
     * Handles any fields or properties annotated with PreParameter. These are removed from the parameters
     * given and set into the PreParameter fields.
     *
     * @param   params The parameters.
     * @param   action The action.
     * @param   actionInvocation The action invocation for the URI and anything else that might be
     *          needed.
     */
    protected void handlePreParameters(Parameters params, Object action, ActionInvocation actionInvocation) {
        Set<String> members = expressionEvaluator.getAllMembers(action.getClass());
        for (String member : members) {
            PreParameter annotation = null;
            try {
                annotation = expressionEvaluator.getAnnotation(PreParameter.class, member, action);
            } catch (ExpressionException e) {
                // Ignore
            }
            
            if (annotation != null) {
                Struct struct = params.optional.remove(member);
                if (struct == null) {
                    struct = params.required.remove(member);
                }

                if (struct == null) {
                    continue;
                }
                
                try {
                    expressionEvaluator.setValue(member, action, struct.values, struct.attributes);
                } catch (ConversionException ce) {
                    messageStore.addConversionError(member, actionInvocation.actionURI(), struct.attributes, (Object[]) struct.values);
                }
            }
        }
    }

    /**
     * Sets all of the parameters into the action.
     *
     * @param   params The parameters.
     * @param   action The action.
     * @param   actionInvocation The action invocation for the URI and anything else that might be
     *          needed.
     */
    protected void handleParameters(Parameters params, Object action, ActionInvocation actionInvocation) {
        // First, process the optional
        for (String key : params.optional.keySet()) {
            Struct struct = params.optional.get(key);

            // If there are no values to set, skip it
            if (struct.values == null) {
                continue;
            }

            try {
                expressionEvaluator.setValue(key, action, struct.values, struct.attributes);
            } catch (ConversionException ce) {
                messageStore.addConversionError(key, action.getClass().getName(), struct.attributes, (Object[]) struct.values);
            } catch (ExpressionException ee) {
                // Ignore, these are optional
            }
        }

        // Next, process the required
        for (String key : params.required.keySet()) {
            Struct struct = params.required.get(key);

            // If there are no values to set, skip it
            if (struct.values == null) {
                continue;
            }

            try {
                expressionEvaluator.setValue(key, action, struct.values, struct.attributes);
            } catch (ConversionException ce) {
                messageStore.addConversionError(key, actionInvocation.actionURI(), struct.attributes, (Object[]) struct.values);
            } catch (ExpressionException ee) {
                String env = resolver.getEnvironment();
                if (env.startsWith("dev")) {
                    throw ee;
                }

                logger.log(Level.FINE, "Invalid parameter to action [" + action.getClass().getName() + "]", ee);
            }
        }
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

    private void addUncheckedValues(Map<String, String[]> map, Parameters parameters) {
        for (String key : map.keySet()) {
            String[] values = map.get(key);
            // Only add the values if there is a single one and it is empty, which denotes that they
            // want to set null into the action, or the values are multiple and they is one non-empty
            // value in the bunch. The second case occurs when they are using multiple checkboxes or
            // radio buttons for the same name. This will cause multiple hidden inputs and they should
            // all either have a unchecked value or should all be empty. If they are all empty, then
            // null should be put into the object.
            if ((values != null && values.length == 1 && values[0].equals("")) || empty(values)) {
                parameters.required.put(key, new Struct());
            } else {
                parameters.required.put(key, new Struct(values));
            }
        }
    }

    private class Parameters {
        private Map<String, Struct> required = new LinkedHashMap<String, Struct>();
        private Map<String, Struct> optional = new LinkedHashMap<String, Struct>();
    }

    private class Struct {
        Map<String, String> attributes = new LinkedHashMap<String, String>();
        String[] values;

        private Struct() {
        }

        private Struct(String[] values) {
            this.values = values;
        }
    }
}