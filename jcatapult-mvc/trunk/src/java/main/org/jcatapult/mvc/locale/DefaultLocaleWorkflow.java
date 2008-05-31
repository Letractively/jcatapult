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
package org.jcatapult.mvc.locale;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jcatapult.servlet.WorkflowChain;

/**
 * <p>
 * This is the default LocaleWorkflow implementation.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultLocaleWorkflow implements LocaleWorkflow {
    public static final String LOCALE_KEY = "__jcatapult_locale";

    /**
     * Retrieves the Locale from the session using the LOCALE_KEY constant. This doesn't ever create
     * a session and might return null if there is a session, but it doesn't have a Locale stored in
     * it. If there isn't a session, this falls back to looking in the request for the Locale.
     *
     * @param   request The request used to get the session or check for the Locale in.
     * @return  The Locale or null if there isn't one.
     */
    public Locale getLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return (Locale) request.getAttribute(LOCALE_KEY);
        }

        return (Locale) session.getAttribute(LOCALE_KEY);
    }

    /**
     * Sets the Locale into the session using the LOCALE_KEY constant. This doesn't ever create
     * a session. If there isn't a session, this falls back to storing the Locale in the request.
     *
     * @param   request The request used to get the session or store the Locale in.
     * @param   locale The Locale to store.
     */
    public void setLocale(HttpServletRequest request, Locale locale) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            request.setAttribute(LOCALE_KEY, locale);
        } else {
            session.setAttribute(LOCALE_KEY, locale);
        }
    }

    /**
     * If the request has a Locale that isn't the default, this updates the Locale by calling the
     * {@link #setLocale(HttpServletRequest, Locale)} method. If the request contains the default
     * Locale, this stores the Locale only if there isn't one already stored.
     *
     * @param   request The request used to get the Locale from.
     * @param   response Passed down the chain.
     * @param   chain The chain.
     * @throws  IOException If the chain throws.
     * @throws  ServletException If the chain throws.
     */
    public void perform(HttpServletRequest request, HttpServletResponse response, WorkflowChain chain)
    throws IOException, ServletException {
        Locale locale = request.getLocale();

        // The client wants to change the locale
        if (!locale.equals(Locale.getDefault())) {
            setLocale(request, locale);
        } else {
            // See if there is already a Locale in the session and if not, put the default one in
            Locale stored = getLocale(request);
            if (stored == null) {
                setLocale(request, locale);
            }
        }

        chain.doWorkflow(request, response);
    }

    public void destroy() {
    }
}