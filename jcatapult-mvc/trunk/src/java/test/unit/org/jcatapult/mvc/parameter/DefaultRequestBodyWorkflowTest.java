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
package org.jcatapult.mvc.parameter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.*;
import net.java.io.FileTools;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;
import org.jcatapult.mvc.servlet.MockWorkflowChain;
import org.jcatapult.mvc.util.RequestKeys;
import org.jcatapult.servlet.WorkflowChain;
import org.jcatapult.test.Capture;
import org.junit.Test;

/**
 * <p>
 * This tests the file upload workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class DefaultRequestBodyWorkflowTest {
    @Test
    public void noFiles() throws IOException, ServletException {
        String body = FileTools.read("src/java/test/unit/org/jcatapult/mvc/parameter/http-test-body-no-files.txt").toString();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded").times(2);
        EasyMock.expect(request.getInputStream()).andReturn(new MockServletInputStream(body));
        EasyMock.expect(request.getCharacterEncoding()).andReturn("UTF-8");
        EasyMock.replay(request);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        DefaultRequestBodyWorkflow workflow = new DefaultRequestBodyWorkflow(wrapper);
        workflow.perform(chain);

        assertEquals(wrapper.getParameter("param1"), "value1");
        assertEquals(wrapper.getParameter("param2"), "value2");

        EasyMock.verify(request, chain);
    }

    @Test
    public void singleFiles() throws IOException, ServletException {
        String body = FileTools.read("src/java/test/unit/org/jcatapult/mvc/parameter/http-test-body-single-file.txt").toString();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getContentType()).andReturn("multipart/form-data, boundary=AaB03x").times(2);
        EasyMock.expect(request.getInputStream()).andReturn(new MockServletInputStream(body));
        EasyMock.expect(request.getCharacterEncoding()).andReturn("UTF-8");
        EasyMock.expect(request.getContentLength()).andReturn(body.length());
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap());
        final Capture capture = new Capture();
        request.setAttribute(eq(RequestKeys.FILE_ATTRIBUTE), capture.<Object>capture());
        EasyMock.replay(request);

        final AtomicBoolean run = new AtomicBoolean(false);
        MockWorkflowChain chain = new MockWorkflowChain(new Runnable() {
            @Override
            public void run() {
                Map<String, List<FileInfo>> files = (Map<String, List<FileInfo>>) capture.object;
                assertEquals(1, files.size());
                try {
                    assertEquals(FileTools.read(files.get("userfile").get(0).file).toString(), "test");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                run.set(true);
            }
        });

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        DefaultRequestBodyWorkflow workflow = new DefaultRequestBodyWorkflow(wrapper);
        workflow.perform(chain);
        assertTrue(run.get());

        assertEquals(wrapper.getParameter("field1"), "value1");
        assertEquals(wrapper.getParameter("field2"), "value2");

        EasyMock.verify(request);
    }

    @Test
    public void multipleFiles() throws IOException, ServletException {
        String body = FileTools.read("src/java/test/unit/org/jcatapult/mvc/parameter/http-test-body-multiple-files.txt").toString();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getContentType()).andReturn("multipart/form-data, boundary=AaB03x").times(2);
        EasyMock.expect(request.getInputStream()).andReturn(new MockServletInputStream(body));
        EasyMock.expect(request.getCharacterEncoding()).andReturn("UTF-8");
        EasyMock.expect(request.getContentLength()).andReturn(body.length());
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap());
        final Capture capture = new Capture();
        request.setAttribute(eq(RequestKeys.FILE_ATTRIBUTE), capture.<Object>capture());
        EasyMock.replay(request);

        final AtomicBoolean run = new AtomicBoolean(false);
        MockWorkflowChain chain = new MockWorkflowChain(new Runnable() {
            @Override
            public void run() {
                Map<String, List<FileInfo>> files = (Map<String, List<FileInfo>>) capture.object;
                assertEquals(1, files.size());
                try {
                    assertEquals(FileTools.read(files.get("userfiles").get(0).file).toString(), "test");
                    assertEquals(FileTools.read(files.get("userfiles").get(1).file).toString(), "test2");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                run.set(true);
            }
        });

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        DefaultRequestBodyWorkflow workflow = new DefaultRequestBodyWorkflow(wrapper);
        workflow.perform(chain);
        assertTrue(run.get());

        assertEquals(wrapper.getParameter("field1"), "value1");
        assertEquals(wrapper.getParameter("field2"), "value2");

        EasyMock.verify(request);
    }

    public class MockServletInputStream extends ServletInputStream {
        private final String body;
        private int index = 0;

        public MockServletInputStream(String body) {
            this.body = body;
        }

        public int read() throws IOException {
            if (index == body.length()) {
                return -1;
            }

            return body.codePointAt(index++);
        }
    }
}
