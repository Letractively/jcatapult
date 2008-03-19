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

/**
 * <p>
 * This tests the JCE password encryptor.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class JCEPasswordEncryptorTest {
    @Test
    public void testEncryption() {
        SaltSource ss = EasyMock.createStrictMock(SaltSource.class);
        EasyMock.expect(ss.getSalt()).andReturn("salt");
        EasyMock.replay(ss);

        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getBoolean("jcatapult.security.password-encryptor.encode-base-64", true)).andReturn(true);
        EasyMock.expect(c.getString("jcatapult.security.password-encryptor.algorithm", "MD5")).andReturn("MD5");
        EasyMock.replay(c);

        JCEPasswordEncryptor encryptor = new JCEPasswordEncryptor(ss, c);
        String result = encryptor.encryptPassword("test", null);
        assertEquals("FQZx56X7is5YqqAS3n+bXA==", result);
    }

    @Test
    public void testEncryptionWithObj() {
        Object user = new Object();
        SaltSource ss = EasyMock.createStrictMock(SaltSource.class);
        EasyMock.expect(ss.getSalt(user)).andReturn("salt");
        EasyMock.replay(ss);

        Configuration c = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(c.getBoolean("jcatapult.security.password-encryptor.encode-base-64", true)).andReturn(true);
        EasyMock.expect(c.getString("jcatapult.security.password-encryptor.algorithm", "MD5")).andReturn("MD5");
        EasyMock.replay(c);

        JCEPasswordEncryptor encryptor = new JCEPasswordEncryptor(ss, c);
        String result = encryptor.encryptPassword("test", user);
        assertEquals("FQZx56X7is5YqqAS3n+bXA==", result);
    }
}