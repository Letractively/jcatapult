/*
 * Copyright (c) 2001-2008, Jcatapult, All Rights Reserved
 */
package org.jcatapult.module.user.action.admin.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import net.java.error.ErrorList;

import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.service.UserService;

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
        user.getAddresses().put("work", new Address());

        Map<String, Integer[]> associations = new HashMap<String, Integer[]>();
        associations.put("roles", new Integer[]{1, 2, 3});

        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(service.validate(user, associations, false, "p", "pc")).andReturn(new ErrorList());
        EasyMock.expect(service.persist(user, associations, "p")).andReturn(true);
        EasyMock.replay(service);

        Add add = new Add();
        add.setServices(null, null, service);
        add.user = user;
        add.associations = associations;
        add.password = "p";
        add.passwordConfirm = "pc";
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

        Map<String, List<?>> items = new HashMap<String, List<?>>();
        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(service.getAssociationObjects()).andReturn(items);
        EasyMock.expect(service.createUser()).andReturn(user);
        EasyMock.replay(service);

        Add add = new Add();
        add.setServices(null, null, service);
        add.prepare();
        assertSame(user, add.user);
        assertSame(items, add.items);

        EasyMock.verify(service);
    }
}