/*
 * Copyright (c) 2007, Inversoft LLC, All Rights Reserved
 */
package com.inversoft.module.user.domain;

import javax.persistence.EntityExistsException;

import static org.junit.Assert.*;
import org.junit.Test;

import com.inversoft.module.user.BaseTest;

/**
 * <p>
 * This class tests the Role entity.
 * </p>
 *
 * @author James Humphrey
 */

public class RoleTest extends BaseTest {
    @Test
    public void testDuplicates() {
        DefaultRole role = new DefaultRole();
        role.setName("admin");
        try {
            persistenceService.persist(role);
            fail("Should have failed");
        } catch (EntityExistsException e) {
            // Expected
        }
    }
}