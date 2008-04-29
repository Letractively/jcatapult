package org.jcatapult.example.webapp.service;

import java.util.List;
import java.sql.SQLException;

import org.jcatapult.test.WebBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;
import org.jcatapult.example.webapp.domain.GuestBookEntry;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class GuestBookEntryServiceImplTest extends WebBaseTest {
    private GuestBookEntryService service;

    @Inject
    public void setService(GuestBookEntryService service) {
        this.service = service;
    }

    @Test
    public void testPersist() throws SQLException {
        clearTable("guest_book_entries");
        makeGuestBookEntry();
    }

    @Test
    public void testFind() {
        List<GuestBookEntry> list = service.find(null);
        assertEquals(1, list.size());
        verify(list.get(0));
    }

    @Test
    public void testDelete() {
        GuestBookEntry guestBookEntry = makeGuestBookEntry();
        service.delete(guestBookEntry.getId());
        GuestBookEntry removed = service.getById(guestBookEntry.getId());
        assertNull(removed);
    }

    @Test
    public void testDeleteMany() {
        GuestBookEntry guestBookEntry = makeGuestBookEntry();
        GuestBookEntry guestBookEntry2 = makeGuestBookEntry();
        GuestBookEntry guestBookEntry3 = makeGuestBookEntry();
        service.deleteMany(new int[]{guestBookEntry.getId(), guestBookEntry2.getId(), guestBookEntry3.getId()});
        GuestBookEntry removed = service.getById(guestBookEntry.getId());
        assertNull(removed);
        removed = service.getById(guestBookEntry2.getId());
        assertNull(removed);
        removed = service.getById(guestBookEntry3.getId());
        assertNull(removed);
    }

    /**
     * Creates an GuestBookEntry. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @return  The GuestBookEntry.
     */
    private GuestBookEntry makeGuestBookEntry() {
        GuestBookEntry guestBookEntry = new GuestBookEntry();
        guestBookEntry.setName("test name");
        guestBookEntry.setComment("test comment");

        service.persist(guestBookEntry);
        return guestBookEntry;
    }

    /**
     * Verifies the test GuestBookEntry.
     *
     * @param   guestBookEntry The test GuestBookEntry.
     */
    private void verify(GuestBookEntry guestBookEntry) {
        assertEquals("test name", guestBookEntry.getName());
        assertEquals("test comment", guestBookEntry.getComment());
    }
}