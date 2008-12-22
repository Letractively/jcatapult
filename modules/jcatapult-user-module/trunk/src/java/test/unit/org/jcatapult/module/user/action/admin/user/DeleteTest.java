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
 *
 */
package org.jcatapult.module.user.action.admin.user;

import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.service.UserService;

/**
 * <p>
 * This class tests the delete action.
 * </p>
 *
 * @author  Scaffolder
 */
public class DeleteTest {
    /**
     * Tests the nothing is deleted if no ids are selected.
     */
    @Test
    public void testNoDelete() {
        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.replay(service);

        Delete delete = new Delete(service);
        String result = delete.execute();
        assertEquals("success", result);
        EasyMock.verify(service);
    }

    /**
     * Tests that all the selected ids are deleted.
     */
    @Test
    public void testDelete() {
        int[] ids = new int[]{1, 2, 3};
        UserService service = EasyMock.createStrictMock(UserService.class);
        service.deleteMany(ids);
        EasyMock.replay(service);

        Delete delete = new Delete(service);
        delete.ids = ids;
        String result = delete.execute();
        assertEquals("success", result);
        EasyMock.verify(service);
    }
}