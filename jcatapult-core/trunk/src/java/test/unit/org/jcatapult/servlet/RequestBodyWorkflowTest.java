/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;
import org.jcatapult.test.servlet.MockServletInputStream;
import org.junit.Test;

/**
 * <p>
 * This class tests the request body workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class RequestBodyWorkflowTest {
    @Test
    public void noContentType() throws IOException, ServletException {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getParameterMap()).andReturn(null);
        expect(request.getContentType()).andReturn(null);
        replay(request);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        RequestBodyWorkflow workflow = new RequestBodyWorkflow(request);
        workflow.perform(chain);

        verify(request, chain);
    }

    @Test
    public void wrongContentType() throws IOException, ServletException {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getParameterMap()).andReturn(null);
        expect(request.getContentType()).andReturn("text/xml");
        replay(request);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        RequestBodyWorkflow workflow = new RequestBodyWorkflow(request);
        workflow.perform(chain);

        verify(request, chain);
    }

    @Test
    public void containerDrainedBody() throws IOException, ServletException {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getParameterMap()).andReturn(new HashMap());
        expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        expect(request.getInputStream()).andReturn(new MockServletInputStream(new byte[0]));
        expect(request.getCharacterEncoding()).andReturn("UTF-8");
        replay(request);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        // This is the assert since the request would be unwrapped if the body contained parameters
        RequestBodyWorkflow workflow = new RequestBodyWorkflow(request);
        workflow.perform(chain);

        verify(request, chain);
    }

    @Test
    public void parse() throws IOException, ServletException {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getParameterMap()).andReturn(new HashMap());
        expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        expect(request.getInputStream()).andReturn(new MockServletInputStream("param1=value1&param2=value2".getBytes()));
        expect(request.getCharacterEncoding()).andReturn("UTF-8");
        replay(request);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        RequestBodyWorkflow workflow = new RequestBodyWorkflow(wrapper);
        workflow.perform(chain);

        Map<String, String[]> params = wrapper.getParameterMap();
        assertEquals(params.size(), 2);
        assertEquals(params.get("param1").length, 1);
        assertEquals(params.get("param1")[0], "value1");
        assertEquals(params.get("param2").length, 1);
        assertEquals(params.get("param2")[0], "value2");

        verify(request, chain);
    }

    @Test
    public void parseMultiple() throws IOException, ServletException {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getParameterMap()).andReturn(new HashMap());
        expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        expect(request.getInputStream()).andReturn(new MockServletInputStream("param1=value1&param1=value2&param2=value3".getBytes()));
        expect(request.getCharacterEncoding()).andReturn("UTF-8");
        replay(request);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        RequestBodyWorkflow workflow = new RequestBodyWorkflow(wrapper);
        workflow.perform(chain);

        Map<String, String[]> params = wrapper.getParameterMap();
        assertEquals(params.size(), 2);
        assertEquals(params.get("param1").length, 2);
        assertEquals(params.get("param1")[0], "value1");
        assertEquals(params.get("param1")[1], "value2");
        assertEquals(params.get("param2").length, 1);
        assertEquals(params.get("param2")[0], "value3");

        verify(request, chain);
    }

    @Test
    public void parseCombine() throws IOException, ServletException {
        Map<String, String[]> oldParams = new HashMap<String, String[]>();
        oldParams.put("param1", new String[]{"oldvalue1", "oldvalue2"});
        oldParams.put("param2", new String[]{"oldvalue3"});

        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getParameterMap()).andReturn(oldParams);
        expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        expect(request.getInputStream()).andReturn(new MockServletInputStream("param1=value1&param1=value2&param2=value3".getBytes()));
        expect(request.getCharacterEncoding()).andReturn("UTF-8");
        replay(request);

        WorkflowChain chain = createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        replay(chain);

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        RequestBodyWorkflow workflow = new RequestBodyWorkflow(wrapper);
        workflow.perform(chain);

        Map<String, String[]> params = wrapper.getParameterMap();
        assertEquals(params.size(), 2);
        assertEquals(params.get("param1").length, 4);
        assertEquals(params.get("param1")[0], "oldvalue1");
        assertEquals(params.get("param1")[1], "oldvalue2");
        assertEquals(params.get("param1")[2], "value1");
        assertEquals(params.get("param1")[3], "value2");
        assertEquals(params.get("param2").length, 2);
        assertEquals(params.get("param2")[0], "oldvalue3");
        assertEquals(params.get("param2")[1], "value3");

        verify(request, chain);
    }
}
