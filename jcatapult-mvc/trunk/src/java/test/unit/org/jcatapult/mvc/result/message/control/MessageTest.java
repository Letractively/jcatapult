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
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.l10n.MessageProvider;
import org.jcatapult.l10n.MissingMessageException;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.result.control.AbstractControlTest;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This class tests the message control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MessageTest extends AbstractControlTest {
    @Test
    public void testMessageAction() {
        MessageProvider provider = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(provider.getMessage("/edit", "key")).andReturn("message");
        EasyMock.replay(provider);

        ActionInvocation ai = new DefaultActionInvocation(new Edit(), "/edit", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultControlBundle")).andReturn(null);
        EasyMock.replay(request);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService("message"));
        message.renderStart(writer, mapNV("key", "key"), null);
        message.renderEnd(writer);
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

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultControlBundle")).andReturn(null);
        EasyMock.replay(request);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService("message"));
        message.renderStart(writer, mapNV("key", "key", "bundle", "bundle"), null);
        message.renderEnd(writer);
        assertEquals("message", writer.toString());
        EasyMock.verify(provider, ais);
    }

    @Test
    public void testMessageFailure() {
        MessageProvider provider = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(provider.getMessage("/edit", "key")).andThrow(new MissingMessageException());
        EasyMock.replay(provider);

        ActionInvocation ai = new DefaultActionInvocation(null, "/edit", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultControlBundle")).andReturn(null);
        EasyMock.replay(request);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService("message"));
        try {
            message.renderStart(writer, mapNV("key", "key"), null);
            message.renderEnd(writer);
            fail("Should have failed");
        } catch (IllegalStateException e) {
            // Expected
        }
        EasyMock.verify(provider, ais);
    }

    @Test
    public void testDefaultMessage() {
        MessageProvider provider = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(provider.getMessage("/edit", "key")).andThrow(new MissingMessageException());
        EasyMock.replay(provider);

        ActionInvocation ai = new DefaultActionInvocation(null, "/edit", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getAttribute("jcatapultControlBundle")).andReturn(null);
        EasyMock.replay(request);

        StringWriter writer = new StringWriter();
        Message message = new Message(provider);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService("message"));
        message.renderStart(writer, mapNV("key", "key", "default", "Message"), null);
        message.renderEnd(writer);
        assertEquals("Message", writer.toString());        
        EasyMock.verify(provider, ais);
    }
}