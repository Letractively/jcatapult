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
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the password control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class PasswordTest extends AbstractInputTest {
    public PasswordTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        AbstractInput input = new Password();
        run(input, null, "password", "foo.bar", "test", "Test",
            mapNV("name", "test", "class", "css-class", "value", "password", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"test\" name=\"test\"/></div>\n" +
            "</div>\n");
    }

    @Test
    public void testAction() {
        AbstractInput input = new Password();
        run(input, new Edit(), "password", "/test", "user.password", "Password",
            mapNV("name", "user.password", "class", "css-class", "value", "password"),
            "<input type=\"hidden\" name=\"user.password@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_password\" class=\"label\">Password</label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"user_password\" name=\"user.password\"/></div>\n" +
            "</div>\n");
    }

    @Test
    public void testFieldErrors() {
        AbstractInput input = new Password();
        run(input, new Edit(), "password", "/test", "user.password", "Password",
            mapNV("name", "user.password", "class", "css-class", "value", "password"),
            "<input type=\"hidden\" name=\"user.password@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_password\" class=\"label\"><span class=\"error\">Password (Password is required, Password must be cool)</span></label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"user_password\" name=\"user.password\"/></div>\n" +
            "</div>\n", "Password is required", "Password must be cool");
    }
}