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
package org.jcatapult.mvc.parameter.fileupload;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.easymock.IArgumentMatcher;
import org.jcatapult.config.Configuration;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.fileupload.annotation.FileUpload;
import org.jcatapult.servlet.WorkflowChain;
import static org.junit.Assert.*;
import org.junit.Test;

import net.java.io.FileTools;

/**
 * <p>
 * This tests the file upload workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultFileUploadWorkflowTest {
    @Test
    public void testNoFiles() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.replay(request);

        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getStringArray("jcatapult.mvc.file-upload.allowed-content-types")).andReturn(new String[0]);
        EasyMock.expect(config.getLong("jcatapult.mvc.file-upload.max-size", 1024000)).andReturn(10l);
        EasyMock.replay(config);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultFileUploadWorkflow workflow = new DefaultFileUploadWorkflow(request, null, null, null, config);
        workflow.perform(chain);

        EasyMock.verify(request, config, chain);
    }

    @Test
    public void testFilesNoAnnotation() throws IOException, ServletException {
        String body = FileTools.read("src/java/test/unit/org/jcatapult/mvc/parameter/fileupload/http-test-body.txt").toString();

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getContentType()).andReturn("multipart/form-data, boundary=AaB03x").times(2);
        EasyMock.expect(request.getInputStream()).andReturn(new MockServletInputStream(body));
        EasyMock.expect(request.getCharacterEncoding()).andReturn("UTF-8");
        EasyMock.expect(request.getContentLength()).andReturn(body.length());
        EasyMock.replay(request);

        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getStringArray("jcatapult.mvc.file-upload.allowed-content-types")).andReturn(new String[0]);
        EasyMock.expect(config.getLong("jcatapult.mvc.file-upload.max-size", 1024000)).andReturn(10l);
        EasyMock.replay(config);

        Object action = new Object();
        ExpressionEvaluator expressionEvaluator = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(expressionEvaluator.getAnnotation(FileUpload.class, "userfile", action)).andReturn(null);
        expressionEvaluator.setValue(eq("userfile"), same(action), capture());
        EasyMock.replay(expressionEvaluator);

        ActionInvocation invocation = EasyMock.createStrictMock(ActionInvocation.class);
        EasyMock.expect(invocation.action()).andReturn(action);
        EasyMock.expect(invocation.uri()).andReturn("/test");
        EasyMock.replay(invocation);

        ActionInvocationStore actionInvocationStore = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(actionInvocationStore.getCurrent()).andReturn(invocation);
        EasyMock.replay(actionInvocationStore);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultFileUploadWorkflow workflow = new DefaultFileUploadWorkflow(new HttpServletRequestWrapper(request), actionInvocationStore, null, expressionEvaluator, config);
        workflow.perform(chain);

        EasyMock.verify(request, config, chain);
    }

    public <T> T capture() {
        reportMatcher(new IArgumentMatcher() {
            public boolean matches(Object argument) {
                FileInfo info = (FileInfo) argument;
                assertNotNull(info);
                assertEquals("text/plain", info.contentType);
                assertEquals("filename", info.name);
                try {
                    assertEquals("test", FileTools.read(info.file).toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            public void appendTo(StringBuffer buffer) {
            }
        });

        return null;
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