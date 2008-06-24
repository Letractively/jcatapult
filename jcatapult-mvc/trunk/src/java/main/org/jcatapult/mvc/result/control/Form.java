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

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.servlet.MVCWorkflow;
import org.jcatapult.mvc.servlet.URIHttpServletRequest;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

/**
 * <p>
 * This is the form control that is used for rendering the open and
 * close form tags.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Form extends AbstractControl {
    private final MVCWorkflow workflow;

    @Inject
    public Form(MVCWorkflow workflow) {
        this.workflow = workflow;
    }

    /**
     * If the user supplied a prepare action URI, that action is inokved.
     *
     * @param   attributes The attributes of the tag.
     * @param   parameterAttributes The parameter attributes.
     * @param   actionInvocation The action invocation.
     */
    @Override
    protected void addAdditionalAttributes(Map<String, Object> attributes, Map<String, String> parameterAttributes,
            ActionInvocation actionInvocation) {
        final String uri = (String) attributes.remove("prepareAction");
        if (uri != null) {
            // Mock out the request for the new URI
            HttpServletRequest old = request;
            URIHttpServletRequest proxy = new URIHttpServletRequest(request, uri);

            // Set in the wrapper
            ServletObjectsHolder.setServletRequest(proxy);

            // Invoke the workflow
            try {
                workflow.perform(new WorkflowChain() {
                    public void continueWorkflow() {
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Change back to the old URI
            proxy = new URIHttpServletRequest(ServletObjectsHolder.getServletRequest(), old.getRequestURI());
            ServletObjectsHolder.setServletRequest(proxy);
        }
    }

    /**
     * @return  form-start.ftl
     */
    protected String startTemplateName() {
        return "form-start.ftl";
    }

    /**
     * @return  form-end.ftl
     */
    protected String endTemplateName() {
        return "form-end.ftl";
    }
}