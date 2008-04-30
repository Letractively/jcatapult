package org.jcatapult.example.webapp.action.guestBook;

import static junit.framework.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.example.webapp.domain.GuestBookEntry;
import org.jcatapult.example.webapp.service.GuestBookEntryService;

/**
 * <p>
 * This class tests the save action.
 * </p>
 *
 * @author  Scaffolder
 */
public class AddTest {
    /**
     * Tests form submission.
     */
    @Test
    public void testSave() {
        GuestBookEntry guestBookEntry = new GuestBookEntry();
        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
        service.persist(guestBookEntry);
        EasyMock.replay(service);

        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.replay(configuration);

        Add add = new Add(service);
        add.setGuestBookEntry(guestBookEntry);
        add.setMethod("POST");

        String result = add.execute();
        assertEquals("success", result);
        EasyMock.verify(service, configuration);
    }
}