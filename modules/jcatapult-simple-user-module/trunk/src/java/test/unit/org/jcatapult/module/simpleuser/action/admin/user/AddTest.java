/*
 * Copyright (c) 2001-2008, Jcatapult, All Rights Reserved
 */
package org.jcatapult.module.simpleuser.action.admin.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.jcatapult.module.simpleuser.BaseTest;
import org.jcatapult.module.simpleuser.domain.DefaultRole;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.persistence.service.PersistenceService;
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
        user.setLogin("test");

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

    /**
     * Tests prepare.
     */
    @Test
    public void testPrepare() {
        DefaultUser user = new DefaultUser();
        user.setLogin("test");

        List<DefaultRole> roles = new ArrayList<DefaultRole>();
        PersistenceService ps = EasyMock.createStrictMock(PersistenceService.class);
        expect(ps.findAllByType(DefaultRole.class)).andReturn(roles);
        replay(ps);

        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(service.createUser()).andReturn(user);
        EasyMock.replay(service);

        Add add = new Add();
        add.setPersistenceService(ps);
        add.setServices(null, null, service);
        add.prepare();
        assertSame(user, add.user);
        assertSame(roles, add.roles);

        EasyMock.verify(service);
    }
}