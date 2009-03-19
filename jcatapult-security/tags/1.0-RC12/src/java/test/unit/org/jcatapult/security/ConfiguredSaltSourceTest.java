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
package org.jcatapult.security;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;
import org.jcatapult.security.config.DefaultSecurityConfiguration;

/**
 * <p>
 * This tests the configured salt source.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ConfiguredSaltSourceTest {
    @Test
    public void testDefault() {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.salt", "jcatapult")).andReturn("jcatapult");
        EasyMock.replay(c);

        ConfiguredSaltSource ss = new ConfiguredSaltSource(new DefaultSecurityConfiguration(c));
        assertEquals("jcatapult", ss.getSalt());
    }

    @Test
    public void testConfigured() {
        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getString("jcatapult.security.salt", "jcatapult")).andReturn("foo");
        EasyMock.replay(c);

        ConfiguredSaltSource ss = new ConfiguredSaltSource(new DefaultSecurityConfiguration(c));
        assertEquals("foo", ss.getSalt());
    }
}