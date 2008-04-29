package org.jcatapult.example.webapp.action.guestBook;

import static junit.framework.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.example.webapp.domain.GuestBookEntry;
import org.jcatapult.example.webapp.service.GuestBookEntryService;
import org.junit.Test;

/**
 * <p>
 * This class tests the edit action.
 * </p>
 *
 * @author  Scaffolder
 */
public class EditTest {
    /**
     * Tests edit fails when the id is invalid.
     */
    @Test
    public void testNoEdit() {
        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
        EasyMock.expect(service.getById(1)).andReturn(null);
        EasyMock.replay(service);

        Edit edit = new Edit(service);
        edit.setId(1);
        String result = edit.execute();
        assertEquals("error", result);
        assertEquals(1, edit.getActionErrors().size());
        EasyMock.verify(service);
    }

    /**
     * Tests that all the selected ids are deleted.
     */
    @Test
    public void testEdit() {
        GuestBookEntry guestBookEntry = new GuestBookEntry();


        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
        EasyMock.expect(service.getById(1)).andReturn(guestBookEntry);
        EasyMock.replay(service);

        Edit edit = new Edit(service);
        edit.setId(1);
        String result = edit.execute();
        assertEquals("success", result);
        assertEquals(0, edit.getActionErrors().size());


        EasyMock.verify(service);
    }

    /**
     * Tests form submission.
     */
    @Test
    public void testPost() {

        GuestBookEntry guestBookEntry = new GuestBookEntry();
        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
        service.persist(guestBookEntry);
        EasyMock.replay(service);

        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.replay(configuration);

        Edit edit = new Edit(service);
        edit.setGuestBookEntry(guestBookEntry);
        edit.setMethod("POST");

        String result = edit.execute();
        assertEquals("success", result);
        EasyMock.verify(service, configuration);
    }
}