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
package org.jcatapult.mvc.result.jsp.control;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.easymock.EasyMock;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.jcatapult.mvc.servlet.MVCWorkflow;
import org.jcatapult.mvc.result.form.control.Form;
import org.jcatapult.mvc.result.control.AbstractControlTest;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.servlet.WorkflowChain;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the form control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FormTest extends AbstractControlTest {
    @Test
    public void testNoPrepare() {
        Form form = new Form(null);
        FreeMarkerService fms = new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver()));
        form.setServices(Locale.US, makeRequest(), makeActionInvocationStore(null, "/test"),
            fms);
        StringWriter writer = new StringWriter();
        form.renderStart(writer, mapNV("action", "/test", "method", "POST"), new HashMap<String, String>());
        form.renderEnd(writer);
        assertEquals(
            "<div class=\"form\">\n" +
            "<form action=\"/test\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n", writer.toString());
    }

    @Test
    public void testPrepare() throws IOException, ServletException {
        final AtomicBoolean called = new AtomicBoolean(false);
        MVCWorkflow workflow = new MVCWorkflow() {
            public void perform(WorkflowChain workflowChain) {
                called.set(true);
                assertEquals("/prepare", ServletObjectsHolder.getServletRequest().getRequestURI());
            }
        };

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getRequestURI()).andReturn("/test");
        EasyMock.replay(request);

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        ServletObjectsHolder.setServletRequest(wrapper);

        Form form = new Form(workflow);
        FreeMarkerService fms = new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver()));
        form.setServices(Locale.US, wrapper, makeActionInvocationStore(null, "/test"), fms);
        StringWriter writer = new StringWriter();
        form.renderStart(writer, mapNV("action", "/test", "method", "POST", "prepareAction", "/prepare"), new HashMap<String, String>());
        form.renderEnd(writer);
        assertEquals(
            "<div class=\"form\">\n" +
            "<form action=\"/test\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n", writer.toString());
        assertTrue(called.get());
        assertEquals("/test", ServletObjectsHolder.getServletRequest().getRequestURI());

        EasyMock.verify(request);
    }

    protected ContainerResolver makeContainerResolver() {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/form-start_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/form-start.ftl");
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/form-end_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/form-end.ftl");
        EasyMock.replay(containerResolver);
        return containerResolver;
    }
}