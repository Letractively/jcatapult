package com.inversoft.module.user.action.admin.user;

import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;

import com.inversoft.module.user.service.UserService;

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