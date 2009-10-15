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

import java.net.URI;
import java.net.URISyntaxException;
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
    public void saveRequest(HttpServletRequest request, String uri, Map<String, String[]> parameters) {
        // Save the request
        SavedHttpRequest saved = new SavedHttpRequest(uri, parameters);
        HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_KEY, saved);
    }

    /**
     * {@inheritDoc}
     */
    public void saveRequest(HttpServletRequest request) {
        Map<String, String[]> requestParameters = null;
        String redirectURI;
        if (request.getMethod().equals("GET")) {
            try {
                Map<String, String[]> params = request.getParameterMap();
                URI uri = new URI(request.getRequestURL().toString() + makeQueryString(params));
                redirectURI = uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : "") +
                    (uri.getFragment() != null ? "#" + uri.getFragment() : "");
            } catch (URISyntaxException e) {
                redirectURI = request.getRequestURI();
            }
        } else {
            requestParameters = request.getParameterMap();
            redirectURI = request.getRequestURI();
        }

        // Save the request
        saveRequest(request, redirectURI, requestParameters);
    }

    /**
     * Converts the parameters map into a query string.
     *
     * @param   parameters The parameters.
     * @return  Either an empty String if the parameters is empty or a RFC compliant query String.
     */
    private String makeQueryString(Map<String, String[]> parameters) {
        if (parameters.size() == 0) {
            return "";
        }

        StringBuilder build = new StringBuilder("?");
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            for (String value : entry.getValue()) {
                build.append(entry.getKey()).append("=").append(value);
            }
        }

        return build.toString();
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

            // Only set a saved request back in if it has some parameters, otherwise it was probably
            // a GET and we don't need anything special.
            if (saved.parameters.size() > 0) {
                session.setAttribute(POST_LOGIN_KEY, saved);
            }

            return saved.uri;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public HttpServletRequest mockSavedRequest(HttpServletRequest request) {
        // See if there is a saved request
        HttpSession session = request.getSession(false);
        if (session == null) {
            return request;
        }
        
        SavedHttpRequest saved = (SavedHttpRequest) session.getAttribute(POST_LOGIN_KEY);
        if (saved != null && SecurityContext.getCurrentUser() != null) {
            session.removeAttribute(POST_LOGIN_KEY);
            return new FacadeHttpServletRequest(request, null, saved.parameters, true);
        }

        return request;
    }

    /**
     * {@inheritDoc}
     */
    public SavedHttpRequest getSavedRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (SavedHttpRequest) session.getAttribute(LOGIN_KEY);
    }
}