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
package org.jcatapult.mvc.scope;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

/**
 * <p>
 * This is the flash scope which stores values in the HttpSession inside
 * a Map under the flash key <code>jcatapultFlash</code>. It fetches
 * values from both the HttpServletRequest and HttpSession under that key.
 * This allows for flash objects to be migrated from the session to the request
 * during request handling so that they are not persisted in the session
 * forever. However, it also allows flash values to be retrieved during the
 * initial request from the session.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class FlashScope implements Scope {
    public static final String FLASH_KEY = "jcatapultFlash";
    private final HttpServletRequest request;

    @Inject
    public FlashScope(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public Object get(String fieldName) {
        Map<String, Object> flash = (Map<String, Object>) request.getAttribute(FLASH_KEY);
        if (flash == null || !flash.containsKey(fieldName)) {
            flash = (Map<String, Object>) request.getSession().getAttribute(FLASH_KEY);
            if (flash == null) {
                return null;
            }
        }

        return flash.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String fieldName, Object value) {
        Map<String, Object> flash = (Map<String, Object>) request.getSession().getAttribute(FLASH_KEY);
        if (flash == null) {
            flash = new HashMap<String, Object>();
            request.getSession().setAttribute(FLASH_KEY, flash);
        }

        flash.put(fieldName, value);
    }

    /**
     * Moves the flash from the session to the request.
     *
     * @param   request The request used to get the session and possibly store the flash.
     */
    public void transferFlash(HttpServletRequest request) {
        Map<String, Object> flash = (Map<String, Object>) request.getSession().getAttribute(FLASH_KEY);
        if (flash != null) {
            request.getSession().removeAttribute(FLASH_KEY);
            request.setAttribute(FLASH_KEY, flash);
        }
    }
}