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
package org.jcatapult.mvc.result.form.control;

import java.util.Map;

import org.jcatapult.mvc.result.control.AbstractControl;
import org.jcatapult.mvc.result.form.FormPreparer;

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
    private final FormPreparer formPreparer;

    @Inject
    public Form(FormPreparer formPreparer) {
        this.formPreparer = formPreparer;
    }

    /**
     * If the user supplied a prepare action URI, that action is inokved.
     *
     * @param   attributes The attributes of the tag.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    @Override
    protected void addAdditionalAttributes(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        formPreparer.prepare();

        // Move the bundle attribute into the request
        if (attributes.containsKey("bundle")) {
            request.setAttribute("jcatapultControlBundle", attributes.remove("bundle"));
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