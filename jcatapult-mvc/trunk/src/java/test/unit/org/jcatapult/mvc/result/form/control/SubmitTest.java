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

import org.example.action.user.Edit;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.result.control.ControlBaseTest;
import org.junit.Test;

import com.google.inject.Inject;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the submit control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SubmitTest extends ControlBaseTest {
    @Inject public Submit submit;

    @Test
    public void testActionLess() {
        ais.setCurrent(new DefaultActionInvocation(null, "/button", null, null));
        run(submit,
            mapNV("name", "button", "value", "test-value", "class", "css-class", "bundle", "/button-bundle"),
            null, "<input type=\"hidden\" name=\"button@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_button\" value=\"\"/>\n" +
            "<div class=\"submit-button button control\">\n" +
            "<div class=\"submit-button-label button-label control-label\"> </div>\n" +
            "<div class=\"submit-button-container button-container control-container\"><input type=\"submit\" class=\"css-class\" id=\"button\" name=\"button\" value=\"Button-Bundle\"/></div>\n" +
            "</div>\n");
    }

    @Test
    public void testAction() {
        ais.setCurrent(new DefaultActionInvocation(new Edit(), "/button", null, null));
        run(submit,
            mapNV("name", "button", "value", "test-value", "class", "css-class"),
            null, "<input type=\"hidden\" name=\"button@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_button\" value=\"\"/>\n" +
            "<div class=\"submit-button button control\">\n" +
            "<div class=\"submit-button-label button-label control-label\"> </div>\n" +
            "<div class=\"submit-button-container button-container control-container\"><input type=\"submit\" class=\"css-class\" id=\"button\" name=\"button\" value=\"Button\"/></div>\n" +
            "</div>\n");
    }

    @Test
    public void testActionAttribute() {
        ais.setCurrent(new DefaultActionInvocation(new Edit(), "/button", null, null));
        run(submit,
            mapNV("name", "button", "action", "/foo", "value", "test-value", "class", "css-class"),
            null, "<input type=\"hidden\" name=\"button@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_button\" value=\"/foo\"/>\n" +
            "<div class=\"submit-button button control\">\n" +
            "<div class=\"submit-button-label button-label control-label\"> </div>\n" +
            "<div class=\"submit-button-container button-container control-container\"><input type=\"submit\" class=\"css-class\" id=\"button\" name=\"button\" value=\"Button\"/></div>\n" +
            "</div>\n");
    }
}