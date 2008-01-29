package com.example.actions.ns.bean;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import com.example.services.BeanService;

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
        BeanService service = EasyMock.createStrictMock(BeanService.class);
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
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        service.deleteMany(ids);
        EasyMock.replay(service);

        Delete delete = new Delete(service);
        delete.setIds(ids);
        String result = delete.execute();
        assertEquals("success", result);
        EasyMock.verify(service);
    }
}