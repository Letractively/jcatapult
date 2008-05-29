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
 *
 */
package org.jcatapult.mvc.action.result;

import java.lang.annotation.Annotation;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.parameters.el.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * <p>
 * This result performs a servlet forward to a JSP or renders a FreeMarker
 * template depending on the extension of the page.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractResult<U extends Annotation> implements Result<U> {
    protected final LocaleWorkflow localeWorkflow;
    protected final ExpressionEvaluator expressionEvaluator;

    @Inject
    protected AbstractResult(LocaleWorkflow localeWorkflow, ExpressionEvaluator expressionEvaluator) {
        this.localeWorkflow = localeWorkflow;
        this.expressionEvaluator = expressionEvaluator;
    }

    /**
     * If the action invocation isn't null, this returns an instance of the {@link ResultHttpServletRequest}
     * class.
     *
     * @param   invocation The action invocation.
     * @param   request The request.
     * @param   response The response.
     * @return  The wrapped request or the request passed in, depending.
     */
    protected HttpServletRequest wrapRequest(ActionInvocation invocation, HttpServletRequest request,
            HttpServletResponse response) {
        if (invocation != null) {
            Locale locale = localeWorkflow.getLocale(request, response);
            return new ResultHttpServletRequest(request, response, invocation.action(), locale, expressionEvaluator);
        }

        return request;
    }
}