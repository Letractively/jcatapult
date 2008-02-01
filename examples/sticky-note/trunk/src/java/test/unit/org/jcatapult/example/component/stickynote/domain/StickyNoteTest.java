package org.jcatapult.example.component.stickynote.domain;

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
        assertTrue(persistenceService.persist(instance));
    }
}