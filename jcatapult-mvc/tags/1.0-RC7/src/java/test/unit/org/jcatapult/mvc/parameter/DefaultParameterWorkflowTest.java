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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.example.domain.Action;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.servlet.WorkflowChain;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the default parameters workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultParameterWorkflowTest {
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
        values.put("user.name", array("")); // This should be stripped out and the ExpressionEvaluator never called for it

        final HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameterMap()).andReturn(values);
        EasyMock.replay(request);

        ExpressionEvaluator expressionEvaluator = EasyMock.createStrictMock(ExpressionEvaluator.class);
        expressionEvaluator.setValue(eq("user.addresses['home'].city"), same(action), aryEq(array("Boulder")), eq(new HashMap<String, String>()));
        expressionEvaluator.setValue(eq("user.age"), same(action), aryEq(array("32")), eq(map("dateFormat", "MM/dd/yyyy")));
        expressionEvaluator.setValue(eq("user.inches"), same(action), aryEq(array("tall")), eq(new HashMap<String, String>()));
        expectLastCall().andThrow(new ConversionException());
        EasyMock.replay(expressionEvaluator);

        ActionInvocation invocation = EasyMock.createStrictMock(ActionInvocation.class);
        EasyMock.expect(invocation.action()).andReturn(action);
        EasyMock.replay(invocation);

        ActionInvocationStore actionInvocationStore = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(actionInvocationStore.getCurrent()).andReturn(invocation);
        EasyMock.replay(actionInvocationStore);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        messageStore.addConversionError(eq("user.inches"), eq("org.example.domain.Action"), eq(new HashMap<String, String>()), eq("tall"));
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
        EasyMock.expect(request.getParameterMap()).andReturn(values);
        EasyMock.replay(request);

        ExpressionEvaluator expressionEvaluator = EasyMock.createNiceMock(ExpressionEvaluator.class);
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
}