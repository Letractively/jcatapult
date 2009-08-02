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
package org.jcatapult.mvc.util;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This class provides some helper methods for dealing with the request.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class RequestTools {
    public static boolean canUseParameters(HttpServletRequest request) {
        String method = request.getMethod().toLowerCase();
        String contentType = request.getContentType();
        contentType = contentType != null ? contentType.toLowerCase() : "";

        return (!method.equals("post") ||
            (method.equals("post") && (contentType.startsWith("application/x-www-form-urlencoded") || contentType.startsWith("multipart/"))));
    }
}
