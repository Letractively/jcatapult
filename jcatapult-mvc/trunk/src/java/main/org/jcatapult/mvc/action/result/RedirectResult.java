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
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @Inject
    public RedirectResult(ExpressionEvaluator expressionEvaluator, HttpServletResponse response,
                          HttpServletRequest request) {
        super(expressionEvaluator);
        this.response = response;
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Redirect redirect, ActionInvocation invocation) throws IOException, ServletException {
        String page = expand(redirect.uri(), invocation.action());
        String context = request.getContextPath();
        if (context.length() > 0 && page.startsWith("/")) {
            page = context + page;
        }

        boolean perm = redirect.perm();

        response.setStatus(perm ? 301 : 302);
        response.sendRedirect(page);
    }

    public static class RedirectImpl implements Redirect {
        private final String code;
        private final String uri;
        private final boolean perm;

        public RedirectImpl(String uri, String code, boolean perm) {
            this.uri = uri;
            this.code = code;
            this.perm = perm;
        }

        public String code() {
            return code;
        }

        public String uri() {
            return uri;
        }

        public boolean perm() {
            return perm;
        }

        public Class<? extends Annotation> annotationType() {
            return Redirect.class;
        }
    }
}