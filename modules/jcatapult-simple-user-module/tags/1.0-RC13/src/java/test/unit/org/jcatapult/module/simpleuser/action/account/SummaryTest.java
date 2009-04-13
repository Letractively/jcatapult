/*
 * Copyright (c) 2001-2008, Jcatapult, All Rights Reserved
 */
package org.jcatapult.module.simpleuser.action.account;

import org.easymock.EasyMock;
import org.jcatapult.module.simpleuser.BaseTest;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.user.service.UserService;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the summary action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class SummaryTest extends BaseTest {
    @Test
    public void testLoad() {
        DefaultUser user = new DefaultUser();

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByUsername("test")).andReturn(user);
        EasyMock.replay(userService);

        Summary summary = new Summary(userService);
        assertEquals("success", summary.execute());
        assertSame(user, summary.user);
    }
}