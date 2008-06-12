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
package org.jcatapult.mvc.result.control;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.servlet.ServletObjectsHolder;

import com.google.inject.Inject;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;

/**
 * <p>
 * This class is the control for a checkbox.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractControl implements Control, TemplateDirectiveModel {
    private LocaleWorkflow localeWorkflow;
    private ActionMappingWorkflow actionMappingWorkflow;
    private MessageStore messageStore;
    private FreeMarkerService freeMarkerService;

    @Inject
    public void setServices(LocaleWorkflow localeWorkflow, ActionMappingWorkflow actionMappingWorkflow,
            MessageStore messageStore, FreeMarkerService freeMarkerService) {
        this.localeWorkflow = localeWorkflow;
        this.actionMappingWorkflow = actionMappingWorkflow;
        this.messageStore = messageStore;
        this.freeMarkerService = freeMarkerService;
    }

    /**
     * <p>
     * Implements the controls render method that is called directly by the JSP taglibs. This method
     * is the main render point for the control and it uses the {@link FreeMarkerService} to render
     * the control. Sub-classes need to implement a number of methods in order to setup the Map that
     * is passed to FreeMarker as well as determine the name of the template.
     * </p>
     *
     * <p>
     * This places these values in the root map:
     * </p>
     *
     * <ul>
     * <li>attributes - The attributes</li>
     * <li>parameter_attributes - The parameter attributes</li>
     * <li>append_attributes - A FreeMarker method that appends attributes ({@link AppendAttributesMethod})</li>
     * <li>request - The HttpServletRequest</li>
     * <li>session - The HttpSession</li>
     * <li>context - The ServletContext</li>
     * <li>action_invocation - The action invocation</li>
     * <li>action - The action object itself (which might be null)</li>
     * <li>field_messages - Any messages associated with the control</li>
     * <li>field_errors - Any errors associated with the control</li>
     * <li>action_messages - Any messages associated with the current action invocation</li>
     * <li>action_errors - Any errors associated with the current action invocation</li>
     * </ul>
     *
     * @param   request The request.
     * @param   writer The writer to output to.
     * @param   attributes The attributes.
     * @param   parameterAttributes The parameter attributes.
     */
    public void render(HttpServletRequest request, Writer writer, Map<String, Object> attributes,
            Map<String, String> parameterAttributes) {
        ActionInvocation actionInvocation = actionMappingWorkflow.fetch(request);
        Object action = actionInvocation.action();
        Locale locale = localeWorkflow.getLocale(request);

        addAdditionalAttributes(request, attributes, parameterAttributes, actionInvocation, locale);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("attributes", attributes);
        parameters.put("parameter_attributes", parameterAttributes);
        parameters.put("action_invocation", actionInvocation);
        parameters.put("action", action);
        parameters.put("request", request);
        parameters.put("field_messages", messageStore.getFieldMessages(request, MessageType.PLAIN, action));
        parameters.put("field_errors", messageStore.getFieldMessages(request, MessageType.ERROR, action));
        parameters.put("action_messages", messageStore.getActionMessages(request, MessageType.PLAIN, action));
        parameters.put("action_errors", messageStore.getActionMessages(request, MessageType.ERROR, action));
        parameters.put("append_attributes", new AppendAttributesMethod());

        String templateName = "/WEB-INF/control-templates/" + templateName();
        freeMarkerService.render(writer, templateName, parameters, locale);
    }

    /**
     * Chains to the {@link #render(HttpServletRequest, Writer, Map, Map)} method. The request is
     * pulled from the {@link ServletObjectsHolder} and the Writer is pulled from the FreeMarker
     * Environment.
     *
     * @param   env The FreeMarker environment.
     * @param   params The parameters passed to this control in the FTL file.
     * @param   loopVars Loop variables (not really used).
     * @param   body The body of the directive.
     */
    @SuppressWarnings("unchecked")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) {
        HttpServletRequest request = ServletObjectsHolder.getServletRequest();
        Map<String, String> parameterAttributes = new HashMap<String, String>();
        for (Object o : params.keySet()) {
            String key = (String) o;
            if (key.startsWith("_")) {
                parameterAttributes.put(key.substring(1), params.get(key).toString());
            }
        }

        render(request, env.getOut(), params, parameterAttributes);
    }

    /**
     * Sub-classes can implement this method to add additional attributes. This is primarily used
     * by control tags to determine values, checked states, selected options, etc.
     *
     * @param   request The HttpServletRequest.
     * @param   attributes The attributes.
     * @param   actionInvocation The action invocation.
     * @param   locale The Locale.
     * @param   parameterAttributes The parameter attributes.
     */
    protected abstract void addAdditionalAttributes(HttpServletRequest request, Map<String, Object> attributes,
            Map<String, String> parameterAttributes, ActionInvocation actionInvocation, Locale locale);

    /**
     * @return  The name of the FreeMarker template that this control renders.
     */
    protected abstract String templateName();
}