/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.jcatapult.deployer.utils;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author jhumphrey
 */
public class ArchiveUtilsTest {

    @Test
    public void testGetVersionFromArchive() {
        {
            String archive = "users-1.0.tar.gz";
            String version = ArchiveUtils.getVersionFromArchive(archive);
            Assert.assertEquals("1.0", version);
        }
        {
            String archive = "crazy-name-here-1.0.1.tar.gz";
            String version = ArchiveUtils.getVersionFromArchive(archive);
            Assert.assertEquals("1.0.1", version);
        }
        {
            String archive = "moreStuff-yup-more-1.0.3-RC1.tar.gz";
            String version = ArchiveUtils.getVersionFromArchive(archive);
            Assert.assertEquals("1.0.3-RC1", version);
        }

    }
}
