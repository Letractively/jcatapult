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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.example.domain.Action;
import org.example.domain.PreAndPostAction;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.el.ExpressionException;
import org.jcatapult.servlet.WorkflowChain;
import org.jcatapult.test.JCatapultBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the default parameters workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultParameterWorkflowTest extends JCatapultBaseTest {
    @Inject public ExpressionEvaluator expressionEvaluator;
    
    /**
     * Tests the workflow method.
     */
    @Test
    public void testSimpleParameters() throws IOException, ServletException {
        Action action = new Action();

        Map<String, String[]> values = new LinkedHashMap<String, String[]>();
        values.put("user.addresses['home'].city", array("Boulder"));
        values.put("user.age", array("32"));
        values.put("user.age@dateFormat", array("MM/dd/yyyy"));
        values.put("user.inches", array("tall"));
        values.put("user.name", array(""));

        final HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.expect(request.getContentType()).andReturn(null);        
        EasyMock.expect(request.getParameterMap()).andReturn(values);
        EasyMock.replay(request);

        ExpressionEvaluator expressionEvaluator = EasyMock.createStrictMock(ExpressionEvaluator.class);
        expect(expressionEvaluator.getAllMembers(action.getClass())).andReturn(new HashSet<String>());
        expressionEvaluator.setValue(eq("user.addresses['home'].city"), same(action), aryEq(array("Boulder")), eq(new HashMap<String, String>()));
        expressionEvaluator.setValue(eq("user.age"), same(action), aryEq(array("32")), eq(map("dateFormat", "MM/dd/yyyy")));
        expressionEvaluator.setValue(eq("user.inches"), same(action), aryEq(array("tall")), eq(new HashMap<String, String>()));
        expectLastCall().andThrow(new ConversionException());
        expressionEvaluator.setValue(eq("user.name"), same(action), aryEq(array("")), eq(new HashMap<String, String>()));
        EasyMock.replay(expressionEvaluator);

        ActionInvocation invocation = EasyMock.createStrictMock(ActionInvocation.class);
        EasyMock.expect(invocation.action()).andReturn(action);
        EasyMock.expect(invocation.uri()).andReturn("/test");
        EasyMock.replay(invocation);

        ActionInvocationStore actionInvocationStore = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(actionInvocationStore.getCurrent()).andReturn(invocation);
        EasyMock.replay(actionInvocationStore);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        messageStore.addConversionError(eq("user.inches"), eq("/test"), eq(new HashMap<String, String>()), eq("tall"));
        EasyMock.replay(messageStore);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultParameterWorkflow workflow = new DefaultParameterWorkflow(request, actionInvocationStore, messageStore, expressionEvaluator);
        workflow.perform(chain);

        EasyMock.verify(request, expressionEvaluator, invocation, actionInvocationStore, messageStore, chain);
    }

    /**
     * Tests radio buttons and checkboxes.
     */
    @Test
    public void testRadioButtonsCheckBoxes() throws IOException, ServletException {
        Action action = new Action();

        Map<String, String[]> values = new HashMap<String, String[]>();
        values.put("__jc_cb_user.checkbox['null']", array(""));
        values.put("__jc_cb_user.checkbox['default']", array("false"));
        values.put("__jc_rb_user.radio['null']", array(""));
        values.put("__jc_rb_user.radio['default']", array("false"));

        final HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");        
        EasyMock.expect(request.getParameterMap()).andReturn(values);
        EasyMock.replay(request);

        ExpressionEvaluator expressionEvaluator = EasyMock.createNiceMock(ExpressionEvaluator.class);
        expect(expressionEvaluator.getAllMembers(action.getClass())).andReturn(new HashSet<String>());
        expressionEvaluator.setValue(eq("user.checkbox['default']"), same(action), aryEq(array("false")), eq(new HashMap<String, String>()));
        expressionEvaluator.setValue(eq("user.radio['default']"), same(action), aryEq(array("false")), eq(new HashMap<String, String>()));
        EasyMock.replay(expressionEvaluator);

        ActionInvocation invocation = EasyMock.createStrictMock(ActionInvocation.class);
        EasyMock.expect(invocation.action()).andReturn(action);
        EasyMock.replay(invocation);

        ActionInvocationStore actionInvocationStore = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(actionInvocationStore.getCurrent()).andReturn(invocation);
        EasyMock.replay(actionInvocationStore);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(messageStore);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultParameterWorkflow workflow = new DefaultParameterWorkflow(request, actionInvocationStore, messageStore, expressionEvaluator);
        workflow.perform(chain);

        EasyMock.verify(request, expressionEvaluator, invocation, actionInvocationStore, messageStore, chain);
    }

    /**
     * Tests image submit button which will try to set the x and y values into the action, but they
     * should be optional and therefore throw exceptions that are ignored.
     */
    @Test
    public void testImageSubmitButton() throws IOException, ServletException {
        Action action = new Action();

        Map<String, String[]> values = new HashMap<String, String[]>();
        values.put("__jc_a_submit", array(""));
        values.put("submit.x", array("1"));
        values.put("submit.y", array("2"));

        final HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.expect(request.getContentType()).andReturn(null);        
        EasyMock.expect(request.getParameterMap()).andReturn(values);
        EasyMock.replay(request);

        ExpressionEvaluator expressionEvaluator = EasyMock.createNiceMock(ExpressionEvaluator.class);
        expect(expressionEvaluator.getAllMembers(action.getClass())).andReturn(new HashSet<String>());
        expressionEvaluator.setValue(eq("submit.x"), same(action), aryEq(array("1")), eq(new HashMap<String, String>()));
        expectLastCall().andThrow(new ExpressionException("Not property x"));
        expressionEvaluator.setValue(eq("submit.y"), same(action), aryEq(array("2")), eq(new HashMap<String, String>()));
        expectLastCall().andThrow(new ExpressionException("Not property y"));
        EasyMock.replay(expressionEvaluator);

        ActionInvocation invocation = EasyMock.createStrictMock(ActionInvocation.class);
        EasyMock.expect(invocation.action()).andReturn(action);
        EasyMock.replay(invocation);

        ActionInvocationStore actionInvocationStore = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(actionInvocationStore.getCurrent()).andReturn(invocation);
        EasyMock.replay(actionInvocationStore);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(messageStore);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultParameterWorkflow workflow = new DefaultParameterWorkflow(request, actionInvocationStore, messageStore, expressionEvaluator);
        workflow.perform(chain);

        EasyMock.verify(request, expressionEvaluator, invocation, actionInvocationStore, messageStore, chain);
    }

    /**
     * Tests that all of the pre and post handling works correctly.
     */
    @Test
    public void testPreAndPost() throws IOException, ServletException {
        PreAndPostAction action = new PreAndPostAction();

        Map<String, String[]> values = new HashMap<String, String[]>();
        values.put("preField", array("1"));
        values.put("preProperty", array("Pre property"));
        values.put("notPre", array("Not pre"));

        final HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.expect(request.getContentType()).andReturn(null);
        EasyMock.expect(request.getParameterMap()).andReturn(values);
        EasyMock.replay(request);

        ActionInvocation invocation = EasyMock.createStrictMock(ActionInvocation.class);
        EasyMock.expect(invocation.action()).andReturn(action);
        EasyMock.replay(invocation);

        ActionInvocationStore actionInvocationStore = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(actionInvocationStore.getCurrent()).andReturn(invocation);
        EasyMock.replay(actionInvocationStore);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(messageStore);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultParameterWorkflow workflow = new DefaultParameterWorkflow(request, actionInvocationStore, messageStore, expressionEvaluator);
        workflow.perform(chain);

        assertTrue(action.preCalled);
        assertTrue(action.postCalled);

        EasyMock.verify(request, invocation, actionInvocationStore, messageStore, chain);
    }
}