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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.el.ExpressionException;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.java.util.Pair;

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
    @SuppressWarnings("unchecked")
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        ActionInvocation actionInvocation = actionInvocationStore.getCurrent();
        Object action = actionInvocation.action();
        String method = request.getMethod().toLowerCase();
        String contentType = request.getContentType();
        contentType = contentType != null ? contentType.toLowerCase() : "";

        if (action != null && (!method.equals("post") ||
                (method.equals("post") && (contentType.startsWith("application/x-www-form-urlencoded") ||
                    contentType.startsWith("multipart/"))))) {
            Map<String, String[]> parameters = request.getParameterMap();

            // First grab the structs and then save them to the request
            Parameters params = getValuesToSet(parameters);

            // Next, process the optional
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
                    messageStore.addConversionError(key, actionInvocation.uri(), struct.attributes, (Object[]) struct.values);
                }
            }
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

    protected Pair<Map<String, FileInfo>, Map<String, String[]>> handleFiles() {
        Map<String, FileInfo> files = new HashMap<String, FileInfo>();
        Map<String, List<String>> params = new HashMap<String, List<String>>();

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List items = upload.parseRequest(request);
            for (Object item : items) {
                FileItem fileItem = (FileItem) item;
                String name = fileItem.getFieldName();
                if (fileItem.isFormField()) {
                    String fileName = fileItem.getName();
                    String contentType = fileItem.getContentType();
                    File file = File.createTempFile("jcatapult", "fileupload");
                    fileItem.write(file);
                    files.put(name, new FileInfo(file, fileName, contentType));
                } else {
                    String value = fileItem.getString();
                    List<String> list = params.get(name);
                    if (list == null) {
                        list = new ArrayList<String>();
                        params.put(name, list);
                    }

                    list.add(value);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to handle file uploads", e);
        }

        Map<String, String[]> finalParams = new HashMap<String, String[]>();
        for (String key : params.keySet()) {
            finalParams.put(key, params.get(key).toArray(new String[params.get(key).size()]));
        }
        return Pair.p(files, finalParams);
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
            if (values != null && values.length == 1 && values[0].equals("")) {
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