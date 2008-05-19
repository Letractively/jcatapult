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
package org.jcatapult.security.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This is a toolkit for common servlet methods.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ServletTools {
    /**
     * Deterimes the URI that can be used in a redirect, which must include the context path.
     *
     * @param   httpRequest Used to get the context path.
     * @param   uri The URI that is appended to the context path.
     * @return  The context based URI.
     */
    public static String getContextURI(HttpServletRequest httpRequest, String uri) {
        String context = httpRequest.getContextPath();
        if (context.equals("")) {
            return uri;
        }

        if (uri.startsWith("/")) {
            return context + uri;
        }

        return context + "/" + uri;
    }
}