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
package org.jcatapult.mvc.action;

import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.example.action.ExecuteMethodThrowsException;
import org.example.action.Extension;
import org.example.action.InvalidExecuteMethod;
import org.example.action.MissingExecuteMethod;
import org.example.action.Post;
import org.example.action.Simple;
import org.jcatapult.mvc.action.config.DefaultActionConfiguration;
import org.jcatapult.mvc.action.result.DefaultResultInvocation;
import org.jcatapult.mvc.action.result.ForwardResult;
import org.jcatapult.mvc.action.result.Result;
import org.jcatapult.mvc.action.result.ResultInvocation;
import org.jcatapult.mvc.action.result.ResultInvocationProvider;
import org.jcatapult.mvc.action.result.ResultProvider;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.servlet.WorkflowChain;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the default action invocation workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultActionInvocationWorkflowTest {
    @Test
    public void testActionLessWithDefault() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getRequestURI()).andReturn("foo/bar");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        ActionInvocation ai = new DefaultActionInvocation(null, "foo/bar", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        Annotation annotation = new ForwardResult.ForwardImpl("/foo/bar", null);
        ResultInvocation ri = new DefaultResultInvocation(annotation, "/foo/bar", null);
        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup("/foo/bar")).andReturn(ri);
        EasyMock.replay(rip);

        Result result = EasyMock.createStrictMock(Result.class);
        result.execute(annotation, ai);
        EasyMock.replay(result);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.expect(resultProvider.lookup(Forward.class)).andReturn(result);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        workflow.perform(chain);

        EasyMock.verify(request, response, ais, rip, result, resultProvider, chain);
    }

    @Test
    public void testActionLessWithoutDefault() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getRequestURI()).andReturn("foo/bar");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(new DefaultActionInvocation(null, "foo/bar", null, null));
        EasyMock.replay(ais);

        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup("/foo/bar")).andReturn(null);
        EasyMock.replay(rip);

        Result result = EasyMock.createStrictMock(Result.class);
        EasyMock.replay(result);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        workflow.perform(chain);

        EasyMock.verify(request, response, ais, rip, result, resultProvider, chain);
    }

    @Test
    public void testActionWithResult() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        Simple simple = new Simple();
        ActionInvocation invocation = new DefaultActionInvocation(simple, "/foo/bar", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        Annotation annotation = new ForwardResult.ForwardImpl("/foo/bar", "success");
        ResultInvocation ri = new DefaultResultInvocation(annotation, "/foo/bar", "success");
        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup(invocation, "/foo/bar", "success")).andReturn(ri);
        EasyMock.replay(rip);

        Result result = EasyMock.createStrictMock(Result.class);
        result.execute(annotation, invocation);
        EasyMock.replay(result);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.expect(resultProvider.lookup(annotation.annotationType())).andReturn(result);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        workflow.perform(chain);

        EasyMock.verify(request, response, ais, rip, result, resultProvider, chain);
    }

    @Test
    public void testActionSuppressResult() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        Simple simple = new Simple();
        ActionInvocation invocation = new DefaultActionInvocation(simple, "/foo/bar", null, null, null, false, true, null );
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.replay(rip);

        Result result = EasyMock.createStrictMock(Result.class);
        EasyMock.replay(result);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        workflow.perform(chain);

        EasyMock.verify(request, response, ais, rip, result, resultProvider, chain);
    }

    @Test
    public void testActionMissingResult() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        response.setStatus(404);
        EasyMock.replay(response);

        Simple simple = new Simple();
        ActionInvocation invocation = new DefaultActionInvocation(simple, "/foo/bar", null, new DefaultActionConfiguration(Simple.class, "/foo/bar"));
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup(invocation, "/foo/bar", "success")).andReturn(null);
        EasyMock.replay(rip);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        try {
            workflow.perform(chain);
            fail("Should have failed with 404");
        } catch (ServletException e) {
            System.out.println(e);
            // Expected
        }

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }

    @Test
    public void testActionMissingResultType() throws IOException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        Simple simple = new Simple();
        ActionInvocation invocation = new DefaultActionInvocation(simple, "/foo/bar", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        Annotation annotation = new ForwardResult.ForwardImpl("/foo/bar", "success");
        ResultInvocation ri = new DefaultResultInvocation(annotation, "/foo/bar", "success");
        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup(invocation, "/foo/bar", "success")).andReturn(ri);
        EasyMock.replay(rip);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.expect(resultProvider.lookup(annotation.annotationType())).andReturn(null);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        try {
            workflow.perform(chain);
            fail("Should have failed");
        } catch (ServletException e) {
            System.out.println(e);
            // Expected
        }

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }

    @Test
    public void testActionWithoutExecuteMethod() throws IOException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        MissingExecuteMethod action = new MissingExecuteMethod();
        ActionInvocation invocation = new DefaultActionInvocation(action, "/foo/bar", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.replay(rip);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        try {
            workflow.perform(chain);
            fail("Should have failed");
        } catch (ServletException e) {
            System.out.println(e);
            // Expected
        }

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }

    @Test
    public void testActionWithWrongReturnType() throws IOException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        InvalidExecuteMethod action = new InvalidExecuteMethod();
        ActionInvocation invocation = new DefaultActionInvocation(action, "/foo/bar", null, new DefaultActionConfiguration(InvalidExecuteMethod.class, "/foo/bar"));
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.replay(rip);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        try {
            workflow.perform(chain);
            fail("Should have failed");
        } catch (ServletException e) {
            System.out.println(e);
            // Expected
        }

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }

    @Test
    public void testActionThatThrowsException() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        ExecuteMethodThrowsException action = new ExecuteMethodThrowsException();
        ActionInvocation invocation = new DefaultActionInvocation(action, "/foo/bar", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.replay(rip);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        try {
            workflow.perform(chain);
            fail("Should have failed");
        } catch (RuntimeException e) {
            System.out.println(e);
            // Expected
        }

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }

    @Test
    public void testActionExtension() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        Extension action = new Extension();
        ActionInvocation invocation = new DefaultActionInvocation(action, "/foo/bar", "json", null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        Annotation annotation = new ForwardResult.ForwardImpl("/foo/bar", "success");
        ResultInvocation ri = new DefaultResultInvocation(annotation, "/foo/bar", "success");
        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup(invocation, "/foo/bar", "success")).andReturn(ri);
        EasyMock.replay(rip);

        Result result = EasyMock.createStrictMock(Result.class);
        result.execute(annotation, invocation);
        EasyMock.replay(result);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.expect(resultProvider.lookup(annotation.annotationType())).andReturn(result);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        workflow.perform(chain);

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }

    @Test
    public void testHttpMethod() throws IOException, ServletException {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.replay(request);
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        Post action = new Post();
        ActionInvocation invocation = new DefaultActionInvocation(action, "/foo/bar", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);

        Annotation annotation = new ForwardResult.ForwardImpl("/foo/bar", "success");
        ResultInvocation ri = new DefaultResultInvocation(annotation, "/foo/bar", "success");
        ResultInvocationProvider rip = EasyMock.createStrictMock(ResultInvocationProvider.class);
        EasyMock.expect(rip.lookup(invocation, "/foo/bar", "success")).andReturn(ri);
        EasyMock.replay(rip);

        Result result = EasyMock.createStrictMock(Result.class);
        result.execute(annotation, invocation);
        EasyMock.replay(result);

        ResultProvider resultProvider = EasyMock.createStrictMock(ResultProvider.class);
        EasyMock.expect(resultProvider.lookup(annotation.annotationType())).andReturn(result);
        EasyMock.replay(resultProvider);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        EasyMock.replay(chain);

        DefaultActionInvocationWorkflow workflow = new DefaultActionInvocationWorkflow(request, response, ais, rip, resultProvider);
        workflow.perform(chain);

        EasyMock.verify(request, response, ais, rip, resultProvider, chain);
    }
}