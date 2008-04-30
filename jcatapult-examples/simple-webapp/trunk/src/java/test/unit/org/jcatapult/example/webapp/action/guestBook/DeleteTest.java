package org.jcatapult.example.webapp.action.guestBook;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.example.webapp.service.GuestBookEntryService;

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
        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
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
        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
        service.deleteMany(ids);
        EasyMock.replay(service);

        Delete delete = new Delete(service);
        delete.setIds(ids);
        String result = delete.execute();
        assertEquals("success", result);
        EasyMock.verify(service);
    }
}