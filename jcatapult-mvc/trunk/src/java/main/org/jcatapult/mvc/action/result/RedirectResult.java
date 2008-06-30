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
package org.jcatapult.mvc.action.result;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * <p>
 * This result performs a HTTP redirect to a URL.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class RedirectResult extends AbstractResult<Redirect> {
    private final HttpServletResponse response;

    @Inject
    public RedirectResult(ExpressionEvaluator expressionEvaluator, HttpServletResponse response) {
        super(expressionEvaluator);
        this.response = response;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Redirect redirect, ActionInvocation invocation) throws IOException, ServletException {
        String page = expand(redirect.uri(), invocation.action());
        boolean perm = redirect.perm();

        response.setStatus(perm ? 301 : 302);
        response.sendRedirect(page);
    }
}