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
import javax.servlet.ServletException;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.result.control.AbstractControlTest;
import org.jcatapult.mvc.result.form.DefaultFormPreparer;
import org.jcatapult.mvc.result.form.FormPreparer;
import org.jcatapult.mvc.result.form.control.Form;
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
        ActionInvocation ai = new DefaultActionInvocation(null, "/test", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        FormPreparer formPreparer = new DefaultFormPreparer(ais);
        Form form = new Form(formPreparer);
        FreeMarkerService fms = new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver()));
        form.setServices(Locale.US, makeRequest(false), makeActionInvocationStore(null, "/test"),
            fms);
        StringWriter writer = new StringWriter();
        form.renderStart(writer, mapNV("action", "/test", "method", "POST"), new HashMap<String, String>());
        form.renderEnd(writer);
        assertEquals(
            "<div class=\"form\">\n" +
            "<form action=\"/test\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n", writer.toString());

        EasyMock.verify(ais);
    }

    @Test
    public void testPrepare() throws IOException, ServletException {
        Edit action = new Edit();
        ActionInvocation ai = new DefaultActionInvocation(action, "/test", null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.getCurrent()).andReturn(ai);
        EasyMock.replay(ais);

        FormPreparer formPreparer = new DefaultFormPreparer(ais);
        Form form = new Form(formPreparer);
        FreeMarkerService fms = new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver()));
        form.setServices(Locale.US, null, makeActionInvocationStore(null, "/test"), fms);
        StringWriter writer = new StringWriter();
        form.renderStart(writer, mapNV("action", "/test", "method", "POST"), new HashMap<String, String>());
        form.renderEnd(writer);
        assertEquals(
            "<div class=\"form\">\n" +
            "<form action=\"/test\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n", writer.toString());
        assertTrue(action.prepared);

        EasyMock.verify(ais);
    }

    protected ContainerResolver makeContainerResolver() {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/form-start_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/form-start.ftl");
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/form-end_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/form-end.ftl");
        EasyMock.replay(containerResolver);
        return containerResolver;
    }
}