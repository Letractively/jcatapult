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
package org.jcatapult.security.login;

import org.easymock.EasyMock;
import org.jcatapult.security.PasswordEncryptor;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the default login service.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultLoginServiceTest {
    @Test
    public void testInvalidLogin() {
        AuthenticationService as = EasyMock.createStrictMock(AuthenticationService.class);
        EasyMock.expect(as.loadUser("test", null)).andReturn(null);
        EasyMock.replay(as);

        DefaultLoginService dls = new DefaultLoginService(as, null, null);
        try {
            dls.login("test", "test", null);
            fail("Should have failed");
        } catch (InvalidUsernameException e) {
            // expect
        } catch (InvalidPasswordException e) {
            fail("Should not have thrown this");
        }

        EasyMock.verify(as);
    }

    @Test
    public void testInvalidPassword() {
        Object user = new Object();
        AuthenticationService as = EasyMock.createStrictMock(AuthenticationService.class);
        EasyMock.expect(as.loadUser("test", null)).andReturn(user);
        EasyMock.replay(as);

        UserAdapter ua = EasyMock.createStrictMock(UserAdapter.class);
        EasyMock.expect(ua.getPassword(user)).andReturn("other-encrypted");
        EasyMock.replay(ua);

        PasswordEncryptor pe = EasyMock.createStrictMock(PasswordEncryptor.class);
        EasyMock.expect(pe.encryptPassword("test", user)).andReturn("encrypted");
        EasyMock.replay(pe);

        DefaultLoginService dls = new DefaultLoginService(as, ua, pe);
        try {
            dls.login("test", "test", null);
            fail("Should have failed");
        } catch (InvalidUsernameException e) {
            fail("Should not have thrown this");
        } catch (InvalidPasswordException e) {
            // expect
        }

        EasyMock.verify(as, ua, pe);
    }

    @Test
    public void testSuccessfulLogin() {
        Object user = new Object();
        AuthenticationService as = EasyMock.createStrictMock(AuthenticationService.class);
        EasyMock.expect(as.loadUser("test", null)).andReturn(user);
        EasyMock.replay(as);

        UserAdapter ua = EasyMock.createStrictMock(UserAdapter.class);
        EasyMock.expect(ua.getPassword(user)).andReturn("encrypted");
        EasyMock.replay(ua);

        PasswordEncryptor pe = EasyMock.createStrictMock(PasswordEncryptor.class);
        EasyMock.expect(pe.encryptPassword("test", user)).andReturn("encrypted");
        EasyMock.replay(pe);

        SecurityContext.setProvider(new JCatapultSecurityContextProvider(ua));
        DefaultLoginService dls = new DefaultLoginService(as, ua, pe);
        try {
            dls.login("test", "test", null);
        } catch (InvalidUsernameException e) {
            fail("Should not have thrown this");
        } catch (InvalidPasswordException e) {
            fail("Should not have thrown this");
        }

        EasyMock.verify(as, ua, pe);
    }
}