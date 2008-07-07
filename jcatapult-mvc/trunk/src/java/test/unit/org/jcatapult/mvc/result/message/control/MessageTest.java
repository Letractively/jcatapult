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
package org.jcatapult.mvc.result.message.control;

import java.io.StringWriter;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.l10n.MessageProvider;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the message control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MessageTest {
    @Test
    public void testMessageAction() {
        MessageProvider provider = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(provider.getMessage("org.example.action.user.Edit", "key")).andReturn("message");
        EasyMock.replay(provider);

        ActionInvocation ai = new DefaultActionInvocation(new Edit(), "/edit", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider, ais);
        message.render(writer, "key", null);
        assertEquals("message", writer.toString());
        EasyMock.verify(provider, ais);
    }

    @Test
    public void testMessageBundle() {
        MessageProvider provider = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(provider.getMessage("bundle", "key")).andReturn("message");
        EasyMock.replay(provider);

        ActionInvocation ai = new DefaultActionInvocation(new Edit(), "/edit", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider, ais);
        message.render(writer, "key", "bundle");
        assertEquals("message", writer.toString());
        EasyMock.verify(provider, ais);
    }

    @Test
    public void testMessageFailure() {
        MessageProvider provider = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.replay(provider);

        ActionInvocation ai = new DefaultActionInvocation(null, "/edit", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider, ais);
        try {
            message.render(writer, "key", null);
            fail("Should have failed");
        } catch (IllegalStateException e) {
            // Expected
        }
        EasyMock.verify(provider, ais);
    }
}