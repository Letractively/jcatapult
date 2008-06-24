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
package org.jcatapult.mvc.servlet;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This request is a URI proxy.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class URIHttpServletRequest extends HttpServletRequestWrapper {
    private final String uri;

    public URIHttpServletRequest(HttpServletRequest httpServletRequest, String uri) {
        super(httpServletRequest);
        this.uri = uri;
    }

    @Override
    public String getRequestURI() {
        return uri;
    }
}