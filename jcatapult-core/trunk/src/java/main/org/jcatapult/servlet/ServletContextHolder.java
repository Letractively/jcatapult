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
public final class ServletContextHolder {
    private static ServletContext servletContext;

    /**
     * Gets the servlet context.
     *
     * @return  The servlet context.
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static void setServletContext(ServletContext servletContext) {
        ServletContextHolder.servletContext = servletContext;
    }
}