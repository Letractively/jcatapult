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
import javax.servlet.ServletException;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.action.config.ActionConfigurationProvider;
import org.jcatapult.mvc.action.config.DefaultActionConfiguration;
import org.jcatapult.servlet.WorkflowChain;
import org.jcatapult.test.Capture;
import org.jcatapult.test.JCatapultBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the default action mapping workflow.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultActionMappingWorkflowTest extends JCatapultBaseTest {
    @Test
    public void testDifferentButtonClick() throws IOException, ServletException {
        request.setUri("/admin/user/edit");
        request.setPost(true);
        request.setParameter("__jc_a_submit", "");
        request.setParameter("__jc_a_cancel", "/admin/user/cancel");
        request.setParameter("cancel", "Cancel");

        run("/admin/user/cancel", null);
    }

    @Test
    public void testDifferentButtonClickRelativeURI() throws IOException, ServletException {
        request.setUri("/admin/user/edit");
        request.setPost(true);
        request.setParameter("__jc_a_submit", "");
        request.setParameter("__jc_a_cancel", "cancel");
        request.setParameter("cancel", "Cancel");

        run("/admin/user/cancel", null);
    }

    @Test
    public void testRequestURI() throws IOException, ServletException {
        request.setUri("/admin/user/edit");
        request.setPost(true);
        request.setParameter("__jc_a_submit", "");
        request.setParameter("__jc_a_cancel", "cancel");
        request.setParameter("submit", "Submit");

        run("/admin/user/edit", null);
    }

    @Test
    public void testExtension() throws IOException, ServletException {
        request.setUri("/admin/user/edit.xml");
        request.setPost(true);
        request.setParameter("__jc_a_submit", "");
        request.setParameter("__jc_a_cancel", "cancel");
        request.setParameter("submit", "Submit");

        run("/admin/user/edit", "xml");
    }

    private void run(String uri, String extension) throws IOException, ServletException {
        ActionConfigurationProvider provider = EasyMock.createStrictMock(ActionConfigurationProvider.class);
        EasyMock.expect(provider.lookup(uri)).andReturn(new DefaultActionConfiguration(Edit.class, uri));
        EasyMock.replay(provider);

        Capture capture = new Capture();
        ActionInvocationStore store = EasyMock.createStrictMock(ActionInvocationStore.class);
        store.setCurrent((ActionInvocation) capture.capture());
        store.popCurrent();
        EasyMock.replay(store);

        ObjectFactory factory = EasyMock.createStrictMock(ObjectFactory.class);
        EasyMock.expect(factory.create(Edit.class)).andReturn(new Edit());
        EasyMock.replay(factory);

        WorkflowChain chain = EasyMock.createStrictMock(WorkflowChain.class);
        chain.continueWorkflow();
        EasyMock.replay(chain);

        DefaultActionMappingWorkflow workflow = new DefaultActionMappingWorkflow(request, response, provider, store, factory);
        workflow.perform(chain);

        ActionInvocation ai = (ActionInvocation) capture.object;
        assertEquals(uri, ai.actionURI());
        assertEquals(extension, ai.extension());
        assertNotNull(ai.configuration());
        assertTrue(ai.executeAction());
        assertTrue(ai.executeResult());

        EasyMock.verify(provider, store, factory, chain);
    }
}