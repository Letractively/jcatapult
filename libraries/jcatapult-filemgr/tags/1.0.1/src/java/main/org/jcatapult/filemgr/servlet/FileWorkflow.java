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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.filemgr.service.FileConfiguration;
import org.jcatapult.servlet.Workflow;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;

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
 * <strong>jcatapult.file-mgr.storage-dir</strong> The location on disk
 * where the files are located.
 * </p>
 *
 * <p>
 * <strong>jcatapult.file-mgr.workflow-prefix</strong> The URI prefix that
 * the FileWorkflow is mapped to in the web.xml file. This is important, because
 * other parts of the JCatapult framework depend on this value. So, it should
 * be set correctly.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileWorkflow implements Workflow {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String prefix;
    private final File location;

    @Inject
    public FileWorkflow(FileConfiguration config, HttpServletRequest request, HttpServletResponse response) {
        this.location = new File(config.getFileStorageDir());
        this.prefix = config.getFileWorkflowPrefix();
        this.request = request;
        this.response = response;
    }

    /**
     * Checks if the URI contains the workflow prefix and if it does, it handles the URI. Otherwise,
     * it calls continueWorkflow on the chain.
     *
     * @param   workflowChain The chain.
     * @throws  IOException
     * @throws  ServletException
     */
    public void perform(WorkflowChain workflowChain) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.startsWith(prefix)) {
            handleFile(uri);
        } else {
            workflowChain.continueWorkflow();
        }
    }

    /**
     * Writes the file out.
     *
     * @param   uri The file URI.
     * @throws  IOException If the write fails.
     */
    protected void handleFile(String uri) throws IOException {
        uri = uri.substring(prefix.length() + 1);
        uri = URLDecoder.decode(uri, "UTF-8");

        File file = new File(location, uri);
        if (!file.exists()) {
            response.setStatus(404);
            return;
        } else if (file.isDirectory()) {
            byte[] ba = "Directory listings disabled.".getBytes("UTF-8");
            response.setStatus(200);
            response.setContentType("text/html");
            response.setContentLength(ba.length);
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(ba);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            return;
        }

        response.setContentLength((int) file.length());

        FileInputStream fis = new FileInputStream(file);
        ServletOutputStream sos = response.getOutputStream();
        InputStream inputStream = new BufferedInputStream(fis);
        BufferedOutputStream bof = new BufferedOutputStream(sos);

        try {
            // Then output the file
            byte[] b = new byte[8192];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                bof.write(b, 0, len);
            }
        } finally {
            bof.close();
        }

        bof.flush();
    }
}