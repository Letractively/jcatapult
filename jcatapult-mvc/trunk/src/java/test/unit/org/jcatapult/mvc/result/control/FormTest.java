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
package org.jcatapult.mvc.result.control;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

import org.easymock.EasyMock;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
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
        Form form = new Form();
        FreeMarkerService fms = new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver()));
        form.setServices(Locale.US, makeRequest(), makeActionInvocationStore(null, "/test"),
            makeMessageStore(null), fms);
        StringWriter writer = new StringWriter();
        form.renderStart(writer, mapNV("action", "/test", "method", "POST"), new HashMap<String, String>());
        form.renderEnd(writer);
        assertEquals(
            "<div class=\"form\">\n" +
            "<form action=\"/test\" method=\"POST\"></form>\n" +
            "</div>", writer.toString());
    }

    protected ContainerResolver makeContainerResolver() {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/form-start_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/form-start.ftl");
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/form-end_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/form-end.ftl");
        EasyMock.replay(containerResolver);
        return containerResolver;
    }
}