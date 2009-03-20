/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.module.user.util;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This class helps with URL building.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class URLTools {
    /**
     * Builds a URL for the server.
     *
     * @param   request The request.
     * @param   uri The URI to append to the URL.
     * @return  The URL.
     */
    public static String makeURL(HttpServletRequest request, String uri) {
        StringBuilder build = new StringBuilder();
        build.append(request.getScheme()).append("://").append(request.getServerName());
        if ((request.getScheme().equals("http") && request.getServerPort() != 80) ||
                (request.getScheme().equals("https") && request.getServerPort() != 443)) {
            build.append(":").append(request.getServerPort());
        }
        build.append("/").append(uri);
        return build.toString();
    }
}
