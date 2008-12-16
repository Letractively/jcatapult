/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.security;

import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

import com.inversoft.module.user.BaseTest;
import com.inversoft.module.user.domain.DefaultUser;

/**
 * <p>
 * This class test the JCatapult security integration.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultAuthenticationServiceTest extends BaseTest {
    private DefaultAuthenticationService service;

    @Inject
    public void setService(DefaultAuthenticationService service) {
        this.service = service;
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        makeUser("test-auth@test.com");
        DefaultUser user = (DefaultUser) service.loadUser("test-auth@test.com", null);
        assertNotNull(user);
        org.junit.Assert.assertEquals("test-auth@test.com", user.getLogin());
        org.junit.Assert.assertEquals(1, user.getRoles().size());
        org.junit.Assert.assertEquals("user", user.getRoles().iterator().next().getName());

        assertNull(service.loadUser("bad-auth@test.com", null));
    }
}