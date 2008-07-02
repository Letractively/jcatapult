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
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.result.control.AbstractControlTest;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This class tests the field messages control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FieldMessagesTest extends AbstractControlTest {
    @Test
    public void testFieldMessagePlain() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeFieldMessageStore(false, "foo", "error1", "error2");

        StringWriter writer = new StringWriter();
        FieldMessages message = new FieldMessages(ms);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService());
        message.renderStart(writer, mapNV("errors", false), new HashMap<String, String>());
        message.renderEnd(writer);
        assertEquals("<ul class=\"field-errors\">\n" +
            "  <li class=\"field-error\">error1</li>\n" +
            "  <li class=\"field-error\">error2</li>\n" +
            "</ul>\n", writer.toString());
        EasyMock.verify(request, ais, ms);
    }

    @Test
    public void testFieldMessageError() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeFieldMessageStore(true, "foo", "error1", "error2");

        StringWriter writer = new StringWriter();
        FieldMessages message = new FieldMessages(ms);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService());
        message.renderStart(writer, mapNV("errors", true), new HashMap<String, String>());
        message.renderEnd(writer);
        assertEquals("<ul class=\"field-errors\">\n" +
            "  <li class=\"field-error\">error1</li>\n" +
            "  <li class=\"field-error\">error2</li>\n" +
            "</ul>\n", writer.toString());
        EasyMock.verify(request, ais, ms);
    }

    @Test
    public void testFieldMessageNamedMissing() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeFieldMessageStore(true, "bar", "error1", "error2");

        StringWriter writer = new StringWriter();
        FieldMessages message = new FieldMessages(ms);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService());
        message.renderStart(writer, mapNV("errors", true, "fields", "foo"), new HashMap<String, String>());
        message.renderEnd(writer);
        assertEquals("", writer.toString());
        EasyMock.verify(request, ais, ms);
    }

    @Test
    public void testFieldMessageNamed() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeFieldMessageStore(true, "bar", "error1", "error2");

        StringWriter writer = new StringWriter();
        FieldMessages message = new FieldMessages(ms);
        message.setServices(Locale.US, request, ais, makeFreeMarkerService());
        message.renderStart(writer, mapNV("errors", true, "fields", "foo,bar"), new HashMap<String, String>());
        message.renderEnd(writer);
        assertEquals("<ul class=\"field-errors\">\n" +
            "  <li class=\"field-error\">error1</li>\n" +
            "  <li class=\"field-error\">error2</li>\n" +
            "</ul>\n", writer.toString());
        EasyMock.verify(request, ais, ms);
    }

    private DefaultFreeMarkerService makeFreeMarkerService() {
        return new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver("field-messages")));
    }
}