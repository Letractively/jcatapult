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
package org.jcatapult.filemgr.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.guice.GuiceContainer;

import com.google.inject.Injector;
import net.java.io.IOTools;

/**
 * <p>
 * This servlet handles returning files that have been uploaded and stored
 * somewhere on the server. This servlet uses the JCatapult configuration
 * system to locate and return files to the browser. This is only necessary
 * if you are not using an Apache or other type of proxy that can return the
 * files. The configuration properties are:
 * </p>
 *
 * <p>
 * <strong>jcatapult.file-mgr.file-servlet.dir</strong> The location on disk
 * where the files are located.
 * </p>
 *
 * <p>
 * <strong>jcatapult.file-mgr.file-servlet.prefix</strong> The URI prefix that
 * the FileServlet is mapped to in the web.xml file. This is important, because
 * other parts of the JCatapult framework depend on this value. So, it should
 * be set correctly.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileServlet extends HttpServlet {
    private File location;

    @Override
    public void init() throws ServletException {
        Injector injector = GuiceContainer.getInjector();
        if (injector == null) {
            throw new ServletException("It looks like you might have set the FileServlet as a load-on-startup " +
                "servlet. This servlet requires that Guice be initialized so that it can get to the configuration " +
                "system. Therefore, the struts2 filter must be called prior to this servlet being created.");
        }

        Configuration configuration;
        try {
            configuration = injector.getInstance(Configuration.class);
        } catch (Exception e) {
            throw new ServletException("It looks like you do not have an Apache configuration bound into Guice. " +
                "This is used to locate the file directory on the server in order for this servlet to correctly " +
                "serve up files.", e);
        }

        location = new File(configuration.getString("file-mgr.file-servlet.dir"));
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String uri = request.getRequestURI();
        if (uri.startsWith(path)) {
            uri = uri.substring(path.length());
        }

        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        File file = new File(location, uri);
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        ServletOutputStream sos = response.getOutputStream();
        IOTools.write(fis, sos, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        throw new ServletException("Posts for files are not allowed.");
    }
}