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
package org.jcatapult.mvc.locale;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.inject.Singleton;

/**
 * <p>
 * This is the default LocaleProvider implementation.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultLocaleProvider implements LocaleProvider {
    public static final String LOCALE_KEY = "jcatapultLocale";

    /**
     * Looks up the Locale using this search order:
     *
     * <ol>
     * <li>If there is a session, look for an attribute under the {@link #LOCALE_KEY}</li>
     * <li>If there is not a session, look for an request attribute under the {@link #LOCALE_KEY}</li>
     * <li>If the locale hasn't been found, get it from the request</li>
     * </ol>
     *
     * @param   request The request used to get the session or check for the Locale in.
     * @return  The Locale and never null.
     */
    public Locale getLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Locale locale;
        if (session == null) {
            locale = (Locale) request.getAttribute(LOCALE_KEY);
        } else {
            locale = (Locale) session.getAttribute(LOCALE_KEY);
        }

        if (locale == null) {
            locale = request.getLocale();
        }

        return locale;
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
}