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
package org.jcatapult.mvc.jsp.control;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.messages.MessageStore;
import org.jcatapult.mvc.parameters.ParameterWorkflow;
import org.jcatapult.mvc.parameters.el.ExpressionEvaluator;

import com.google.inject.Inject;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>
 * This class is the control for a checkbox.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Checkbox implements Control, TemplateDirectiveModel {
    private final LocaleWorkflow localeWorkflow;
    private final ExpressionEvaluator expressionEvaluator;
    private final ActionMappingWorkflow actionMappingWorkflow;
    private final ParameterWorkflow parameterWorkflow;
    private final MessageStore messageStore;
    private final HttpServletRequest request;

    @Inject
    public Checkbox(LocaleWorkflow localeWorkflow, ExpressionEvaluator expressionEvaluator,
            ActionMappingWorkflow actionMappingWorkflow, ParameterWorkflow parameterWorkflow,
            MessageStore messageStore, HttpServletRequest request) {
        this.localeWorkflow = localeWorkflow;
        this.expressionEvaluator = expressionEvaluator;
        this.actionMappingWorkflow = actionMappingWorkflow;
        this.parameterWorkflow = parameterWorkflow;
        this.messageStore = messageStore;
        this.request = request;
    }

    public void render(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        Map<String, String> paramAttributes = parameterWorkflow.fetchAttributes(request, name);
        ActionInvocation actionInvocation = actionMappingWorkflow.fetch(request);
        Object action = actionInvocation.action();
        Locale locale = localeWorkflow.getLocale(request);

        if (!attributes.containsKey("value") && action != null) {
            String value = expressionEvaluator.getValue(name, action, request, locale, paramAttributes);
            if (value == null) {
                value = (String) attributes.get("defaultValue");
            }

            attributes.put("value", value);
        }

        attributes.put("action", action);
        attributes.put("request", request);
        attributes.put("fieldErrors", messageStore.getFieldErrors(request));
        attributes.put("fieldMessages", messageStore.getFieldMessages(request));
        attributes.put("actionErrors", messageStore.getActionErrors(request));
        attributes.put("actionMessages", messageStore.getActionMessages(request));

        executeTemplate("checkbox", attributes, actionInvocation, locale);
    }

    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
    throws TemplateException, IOException {
        render(params);
    }

    protected void executeTemplate(String templateName, Map<String, Object> attributes,
            ActionInvocation actionInvocation, Locale locale) {
        // run freemarker here
    }
}