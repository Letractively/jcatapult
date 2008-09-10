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
import static java.util.Arrays.*;
import javax.servlet.ServletException;

import org.easymock.EasyMock;
import org.example.action.ComplexRest;
import org.example.action.user.Edit;
import org.example.action.user.RESTEdit;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.action.config.DefaultActionConfiguration;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.servlet.WorkflowChain;
import org.jcatapult.test.JCatapultBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the default URI parmaeter workflow.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultURIParameterWorkflowTest extends JCatapultBaseTest {
    @Inject
    ExpressionEvaluator expressionEvaluator;

    /**
     * Tests the no parameters case.
     *
     * @throws  IOException Never
     * @throws  ServletException Never
     */
    @Test
    public void testNoParameters() throws IOException, ServletException {
        ActionInvocationStore store = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(store.getCurrent()).andReturn(
            new DefaultActionInvocation(new Edit(), "/admin/user/edit", null, new DefaultActionConfiguration(Edit.class, "/admin/user/edit")));
        EasyMock.replay(store);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultURIParameterWorkflow workflow = new DefaultURIParameterWorkflow(expressionEvaluator, store);
        workflow.perform(chain);

        EasyMock.verify(store, chain);
    }

    /**
     * Tests the single parameter case.
     *
     * @throws  IOException Never
     * @throws  ServletException Never
     */
    @Test
    public void testSingleIDParameters() throws IOException, ServletException {
        RESTEdit action = new RESTEdit();
        ActionInvocationStore store = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(store.getCurrent()).andReturn(
            new DefaultActionInvocation(action, "/admin/user/rest-edit/12", null, asList("12"), new DefaultActionConfiguration(RESTEdit.class, "/admin/user/rest-edit"), true, true, null));
        EasyMock.replay(store);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultURIParameterWorkflow workflow = new DefaultURIParameterWorkflow(expressionEvaluator, store);
        workflow.perform(chain);

        assertEquals(12, action.id);

        EasyMock.verify(store, chain);
    }

    /**
     * Tests the complex parameters case.
     *
     * @throws  IOException Never
     * @throws  ServletException Never
     */
    @Test
    public void testComplexParameters() throws IOException, ServletException {
        ComplexRest action = new ComplexRest();
        ActionInvocationStore store = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(store.getCurrent()).andReturn(
            new DefaultActionInvocation(action, "/complex-rest/brian/static/pontarelli/then/a/bunch/of/stuff", null,
                asList("brian", "static", "pontarelli", "then", "a", "bunch", "of", "stuff"),
                new DefaultActionConfiguration(ComplexRest.class, "/complex-rest/brian/static/pontarelli/then/a/bunch/of/stuff"), true, true, null));
        EasyMock.replay(store);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultURIParameterWorkflow workflow = new DefaultURIParameterWorkflow(expressionEvaluator, store);
        workflow.perform(chain);

        assertEquals("brian", action.firstName);
        assertEquals("pontarelli", action.lastName);
        assertEquals("then", action.theRest.get(0));
        assertEquals("a", action.theRest.get(1));
        assertEquals("bunch", action.theRest.get(2));
        assertEquals("of", action.theRest.get(3));
        assertEquals("stuff", action.theRest.get(4));

        EasyMock.verify(store, chain);
    }
}