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
package org.jcatapult.mvc;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.junit.WebappActionTest;
import org.junit.Test;

/**
 * <p>
 * This class tests the MVC from a high level perspective.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class GlobalTest extends WebappActionTest {
    @Test
    public void testWorkflowCall() throws IOException, ServletException {
        test("/user/edit").get();
    }
}