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
package org.jcatapult.dbmgr.component;

import org.junit.Assert;
import org.junit.Test;

import net.java.util.Version;

/**
 * User: jhumphrey
 * Date: Dec 10, 2007
 */
public class ComponentJarToolsTest {

    @Test
    public void testGetVersionFromJarFilename() {
        Version v = ComponentJarTools.getVersionFromJarFilename("component1-1.1.jar");

        Assert.assertEquals(new Version("1.1"), v);
    }
}
