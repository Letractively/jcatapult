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
package org.jcatapult.mvc.result.form.control;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.ServletException;

import org.example.action.user.Edit;
import org.example.action.user.Index;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.result.form.ControlBaseTest;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.google.inject.Inject;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the form control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FormTest extends ControlBaseTest {
    @Inject Form form;

    @Test
    public void testNoPrepare() {
        request.setUri("/user/");
        Index index = new Index();
        ais.setCurrent(new DefaultActionInvocation(index, "/user/", null, null));

        run(form,
            mapNV("action", "/user/", "method", "POST"),
            "<div class=\"form\">\n" +
            "<form action=\"/user/\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n");
    }

    @Test
    public void testPrepare() throws IOException, ServletException {
        request.setUri("/user/edit");
        Edit edit = new Edit();
        ais.setCurrent(new DefaultActionInvocation(edit, "/user/edit", null, null));

        run(form,
            mapNV("action", "/user/edit", "method", "POST"),
            "<div class=\"form\">\n" +
            "<form action=\"/user/edit\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n");
        assertTrue(edit.formPrepared);
    }

    @Test
    public void testActionIsDifferentURI() throws IOException, ServletException {
        request.setUri("/user/");
        Index index = new Index();
        ais.setCurrent(new DefaultActionInvocation(index, "/user/", null, null));

        StringWriter writer = new StringWriter();
        form.renderStart(writer, mapNV("action", "/user/edit", "method", "POST"), map("param", "param-value"));

        Edit edit = (Edit) ais.getCurrent().action();
        assertTrue(edit.formPrepared);

        form.renderEnd(writer);
        assertSame(Index.class, ais.getCurrent().action().getClass());
        assertEquals(
            "<div class=\"form\">\n" +
            "<form action=\"/user/edit\" method=\"POST\">\n" +
            "</form>\n" +
            "</div>\n", writer.toString());
    }
}