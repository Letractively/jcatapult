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
package org.jcatapult.security.saved;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.servlet.FacadeHttpServletRequest;

/**
 * <p>
 * This default implementation of the {@link SavedRequestService}.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultSavedRequestService implements SavedRequestService {
    public static final String LOGIN_KEY = "org.jcatapult.security.servlet.saved.loginSavedHttpRequest";
    public static final String POST_LOGIN_KEY = "org.jcatapult.security.servlet.saved.postLoginSavedHttpRequest";

    /**
     * {@inheritDoc}
     */
    public void saveRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        // Save the request
        String uri = request.getRequestURI();
        Map<String, String[]> requestParameters = request.getParameterMap();
        SavedHttpRequest saved = new SavedHttpRequest(uri, requestParameters);
        session.setAttribute(LOGIN_KEY, saved);
    }

    /**
     * {@inheritDoc}
     */
    public String processSavedRequest(HttpServletRequest request) {
        // See if there is a saved request
        HttpSession session = request.getSession(true);
        SavedHttpRequest saved = (SavedHttpRequest) session.getAttribute(LOGIN_KEY);
        if (saved != null) {
            session.removeAttribute(LOGIN_KEY);
            session.setAttribute(POST_LOGIN_KEY, saved);
            return saved.uri;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpServletRequest mockSavedRequest(HttpServletRequest request) {
        // See if there is a saved request
        HttpSession session = request.getSession(true);
        SavedHttpRequest saved = (SavedHttpRequest) session.getAttribute(POST_LOGIN_KEY);
        if (saved != null && SecurityContext.getCurrentUser() != null) {
            session.removeAttribute(POST_LOGIN_KEY);
            return new FacadeHttpServletRequest(request, null, saved.parameters);
        }

        return request;
    }
}