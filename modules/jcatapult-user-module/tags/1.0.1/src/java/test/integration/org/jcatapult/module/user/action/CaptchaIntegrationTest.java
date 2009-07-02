/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.module.user.action;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.module.user.BaseIntegrationTest;
import org.jcatapult.mvc.test.WebappTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the captcha action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class CaptchaIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testJPG() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/captcha.jpg").get();

        String str = runner.response.getStream().toString();
        assertNotNull(str);

        String text = (String) runner.session.getAttribute("captchaText");
        assertNotNull(text);
    }

    @Test
    public void testGIF() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/captcha.gif").get();

        String str = runner.response.getStream().toString();
        assertNotNull(str);

        String text = (String) runner.session.getAttribute("captchaText");
        assertNotNull(text);
    }

    @Test
    public void testPNG() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/captcha.png").get();

        String str = runner.response.getStream().toString();
        assertNotNull(str);

        String text = (String) runner.session.getAttribute("captchaText");
        assertNotNull(text);
    }
}