package org.jcatapult.example.webapp.domain;

import org.jcatapult.persistence.PersistenceService;
import org.jcatapult.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the GuestBookEntry entity.
 * </p>
 *
 * @author  Scaffolder
 */
public class GuestBookEntryTest extends JPABaseTest {
    private PersistenceService persistenceService;

    @Inject
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Test
    public void testEntity() {
        GuestBookEntry instance = new GuestBookEntry();
        instance.setComment("Comment");
        instance.setName("Name");
        assertTrue(persistenceService.persist(instance));
    }

    @Test
    public void testLookup() {
        GuestBookEntry instance = persistenceService.queryFirst(GuestBookEntry.class,
            "select gbe from GuestBookEntry gbe where gbe.name = ?1", "Name");
        assertNotNull(instance);
        assertEquals("Name", instance.getName());
        assertEquals("Comment", instance.getComment());
    }
}