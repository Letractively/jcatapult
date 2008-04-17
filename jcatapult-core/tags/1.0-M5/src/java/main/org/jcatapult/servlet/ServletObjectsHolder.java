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
package org.jcatapult.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>
 * This class is a static storage location for the servlet context.
 * This is necessary because we need some method of determining the
 * location on disk of the email FreeMarker templates.
 * </p>
 *
 * <p>
 * This class is setup as long as the JCatapultFilter is placed into
 * the web.xml file and is the first filter in the chain.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public final class ServletObjectsHolder {
    private static ServletContext servletContext;
    private static ThreadLocal<ServletRequest> request = new ThreadLocal<ServletRequest>();
    private static ThreadLocal<ServletResponse> response = new ThreadLocal<ServletResponse>();

    /**
     * Gets the servlet context.
     *
     * @return  The servlet context.
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Sets the servlet context.
     *
     * @param   servletContext The servlet context.
     */
    public static void setServletContext(ServletContext servletContext) {
        ServletObjectsHolder.servletContext = servletContext;
    }

    /**
     * Gets the servlet request for the current thread (if any).
     *
     * @return   The servlet request for the current thread.
     */
    public static ServletRequest getServletRequest() {
        return request.get();
    }

    /**
     * Sets the servlet request for the current thread.
     *
     * @param   servletRequest The servlet request for the current thread.
     */
    public static void setServletRequest(ServletRequest servletRequest) {
        request.set(servletRequest);
    }

    /**
     * Removes the servlet request for the current thread.
     */
    public static void clearServletRequest() {
        request.remove();
    }

    /**
     * Gets the servlet response for the current thread (if any).
     *
     * @return   The servlet response for the current thread.
     */
    public static ServletResponse getServletResponse() {
        return response.get();
    }

    /**
     * Sets the servlet response for the current thread.
     *
     * @param   servletResponse The servlet response for the current thread.
     */
    public static void setServletResponse(ServletResponse servletResponse) {
        response.set(servletResponse);
    }

    /**
     * Removes the servlet response for the current thread.
     */
    public static void clearServletResponse() {
        response.remove();
    }
}