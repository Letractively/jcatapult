/*
 * Copyright (c) 2001-2008, Jcatapult, All Rights Reserved
 */
package org.jcatapult.module.user.action.admin.user;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

import static org.easymock.EasyMock.*;
import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.service.UserConfiguration;
import org.jcatapult.user.service.UserService;
import org.junit.Test;

import net.java.error.ErrorList;

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
        user.setEmail("test");
        user.getAddresses().put("work", new Address());

        Map<String, int[]> associations = new HashMap<String, int[]>();
        associations.put("roles", new int[]{1, 2, 3});

        UserService service = createStrictMock(UserService.class);
        expect(service.validate(user, associations, false, "p", "pc")).andReturn(new ErrorList());
        expect(service.persist(user, associations, "p")).andReturn(true);
        replay(service);

        UserConfiguration config = createStrictMock(UserConfiguration.class);
        expect(config.isUsernameSameAsEmail()).andReturn(true);
        replay(config);

        Add add = new Add();
        add.setServices(null, config, service);
        add.user = user;
        add.associations = associations;
        add.password = "p";
        add.passwordConfirm = "pc";
        add.validate();
        String result = add.post();
        assertEquals("success", result);
        verify(service);
    }
}