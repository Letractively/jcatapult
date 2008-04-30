package org.jcatapult.example.stickynote.domain;

import org.jcatapult.persistence.PersistenceService;
import org.jcatapult.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the StickyNote entity.
 * </p>
 *
 * @author  Scaffolder
 */
public class StickyNoteTest extends JPABaseTest {
    private PersistenceService persistenceService;

    @Inject
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Test
    public void testEntity() {
        StickyNote instance = new StickyNote();
        instance.setNote("Note");
        assertTrue(persistenceService.persist(instance));
    }

    @Test
    public void testQuery() {
        StickyNote instance = persistenceService.queryFirst(StickyNote.class,
            "select sn from StickyNote sn where sn.note = ?1", "Note");
        assertNotNull(instance);
        assertEquals("Note", instance.getNote());
    }
}