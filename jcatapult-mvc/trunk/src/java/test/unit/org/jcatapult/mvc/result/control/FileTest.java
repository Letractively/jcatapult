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

import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the file control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileTest extends AbstractInputTest {
    public FileTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        AbstractInput input = new File();
        run(input, null, "file", "foo.bar", "test", "Test",
            mapNV("name", "test", "value", "test-value", "class", "css-class", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><input type=\"file\" class=\"css-class\" id=\"test\" name=\"test\"/></div>\n" +
            "</div>");
    }

    @Test
    public void testAction() {
        AbstractInput input = new File();
        run(input, null, "file", "org.example.action.user.Edit", "user.profie", "Your profile",
            mapNV("name", "user.profile", "class", "css-class"),
            "<input type=\"hidden\" name=\"user.profile@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your profile</label></div>\n" +
            "<div class=\"control-container\"><input type=\"file\" class=\"css-class\" id=\"user_name\" name=\"user.profile\"/></div>\n" +
            "</div>");
    }

    @Test
    public void testFieldErrors() {
        AbstractInput input = new File();
        run(input, null, "file", "org.example.action.user.Edit", "user.profie", "Your profile",
            mapNV("name", "user.profile", "class", "css-class"),
            "<input type=\"hidden\" name=\"user.profile@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\"><span class=\"error\">Your profile (Profile is required, Profile must be cool)</span></label></div>\n" +
            "<div class=\"control-container\"><input type=\"file\" class=\"css-class\" id=\"user_name\" name=\"user.profile\"/></div>\n" +
            "</div>", "Profile is required", "Profile must be cool");
    }
}