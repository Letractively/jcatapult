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
package org.jcatapult.test;

import org.junit.Ignore;

/**
 * @deprecated  Use ModuleBaseTest
 */
@Ignore
@Deprecated
public abstract class ComponentBaseTest extends WebBaseTest {

    /**
     * Returns the directory of the web root for a component which is currently the
     * <code>src/web/test</code> directory. This is done since the <code>src/web/main</code>
     * directory really only contains FreeMarker templates and the test directory might contain
     * configuration files used for testing.
     *
     * @return  The component web root directory.
     */
    protected String getWebRootDir() {
        return "src/web/test";
    }
}