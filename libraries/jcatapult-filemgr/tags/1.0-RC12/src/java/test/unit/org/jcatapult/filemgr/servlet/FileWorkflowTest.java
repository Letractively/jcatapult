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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.easymock.EasyMock.*;
import org.jcatapult.config.Configuration;
import org.jcatapult.filemgr.service.DefaultFileConfiguration;
import org.jcatapult.servlet.WorkflowChain;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class tests the file servlet.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FileWorkflowTest {
    @Test
    public void testMissingFile() throws ServletException, IOException {
        final Configuration configuration = createStrictMock(Configuration.class);
        expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
            andReturn("src/java/test/unit/org/jcatapult/filemgr/servlet");
        expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/files");
        replay(configuration);

        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getRequestURI()).andReturn("/files/missing-file.xml");
        replay(request);

        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        response.setStatus(404);
        replay(response);

        FileWorkflow fs = new FileWorkflow(new DefaultFileConfiguration(configuration), request, response);
        fs.perform(null);
        verify(request, response);
    }

    @Test
    public void testSuccess() throws ServletException, IOException {
        final Configuration configuration = createStrictMock(Configuration.class);
        expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
            andReturn("src/java/test/unit/org/jcatapult/filemgr/servlet");
        expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/files");
        replay(configuration);

        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getRequestURI()).andReturn("/files/test-file.xml");
        replay(request);

        final StringBuilder build = new StringBuilder();
        ServletOutputStream sos = new ServletOutputStream() {
            public void write(int b) throws IOException {
                build.append(new String(new int[]{b}, 0, 1));
            }
        };

        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        response.setContentLength(5);
        expect(response.getOutputStream()).andReturn(sos);
        replay(response);

        FileWorkflow fs = new FileWorkflow(new DefaultFileConfiguration(configuration), request, response);
        fs.perform(null);

        Assert.assertEquals("hello", build.toString());
        verify(request, response);
    }

    @Test
    public void testWrongPrefix() throws ServletException, IOException {
        final Configuration configuration = createStrictMock(Configuration.class);
        expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
            andReturn("src/java/test/unit/org/jcatapult/filemgr/servlet");
        expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/files");
        replay(configuration);

        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getRequestURI()).andReturn("/something-else/test-file.xml");
        replay(request);

        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        replay(response);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        FileWorkflow fs = new FileWorkflow(new DefaultFileConfiguration(configuration), request, response);
        fs.perform(chain);

        verify(request, response, chain);
    }
}