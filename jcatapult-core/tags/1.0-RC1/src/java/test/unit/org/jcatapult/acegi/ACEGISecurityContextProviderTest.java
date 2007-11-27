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
package org.jcatapult.acegi;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the ACEGI security provider.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ACEGISecurityContextProviderTest {
    @Test
    public void testGetCurrentUsername() throws Exception {
        ACEGISecurityContextProvider provider = new ACEGISecurityContextProvider(new TestUserAdapter());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new TestUser("fred"), "password"));
        String username = provider.getCurrentUsername();
        assertEquals("fred", username);
    }

    public void testLogin() throws Exception {
        ACEGISecurityContextProvider provider = new ACEGISecurityContextProvider(new TestUserAdapter());
        TestUser user = new TestUser("fred");
        provider.login(user);
        assertEquals("fred", provider.getCurrentUsername());

        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context.getAuthentication());
        assertSame(user, context.getAuthentication().getPrincipal());
    }

    public void testLogout() throws Exception {
        testLogin();

        ACEGISecurityContextProvider provider = new ACEGISecurityContextProvider(new TestUserAdapter());
        provider.logout();

        SecurityContext context = SecurityContextHolder.getContext();
        assertNull(context.getAuthentication());
    }
}