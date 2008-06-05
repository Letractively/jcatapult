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

import org.jcatapult.mvc.scope.annotation.Flash;

/**
 * <p>
 * This is the flash scope which fetches and stores values in the
 * HttpSession inside a Map under the flash key. Flash handling requires a
 * number of specific operations to occur in a specific order.
 * </p>
 *
 * <p>
 * First, the flash Map must be pulled out of the session prior to any parameter
 * handling is performed. It also must be pulled out of the session prior to
 * any scope handling.
 * </p>
 *
 * <p>
 * Second, the flash Map must be stored into the request after it has been
 * pulled out of the session.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class FlashScope implements Scope<Flash> {
    public static final String FLASH_KEY = "__jcatapult_scope_flash";

    /**
     * {@inheritDoc}
     */
    public Object get(String fieldName, HttpServletRequest request) {
        Map<String, Object> flash = (Map<String, Object>) request.getAttribute(FLASH_KEY);
        if (flash == null) {
            return null;
        }

        return flash.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String fieldName, HttpServletRequest request, Object value) {
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

    /**
     * {@inheritDoc}
     */
    public Class<Flash> annotationType() {
        return Flash.class;
    }
}