/*
 * Copyright (c) 2001-2008, Jcatapult, All Rights Reserved
 */
package org.jcatapult.module.simpleuser.action.admin.user;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.jcatapult.module.simpleuser.BaseTest;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.user.service.UserService;
import org.junit.Test;

/**
 * <p>
 * This class tests the add action.
 * </p>
 *
 * @author  Scaffolder
 */
public class AddTest extends BaseTest {
    /**
     * Tests add.
     */
    @Test
    public void testAddPost() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        Map<String, int[]> associations = new HashMap<String, int[]>();
        associations.put("roles", new int[]{1, 2, 3});

        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(service.persist(user, associations, "password")).andReturn(true);
        EasyMock.replay(service);

        Add add = new Add();
        add.setServices(null, null, service);
        add.user = user;
        add.associations = associations;
        add.password = "password";
        add.passwordConfirm = "password";
        add.validate();
        String result = add.post();
        assertEquals("success", result);
        EasyMock.verify(service);
    }
}