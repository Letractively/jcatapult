package org.jcatapult.example.component.stickynote.service;

import java.util.List;

import org.jcatapult.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.domain.StickyNote;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class StickyNoteServiceImplTest extends JPABaseTest {
    private StickyNoteService service;

    @Inject
    public void setService(StickyNoteService service) {
        this.service = service;
    }

    @Test
    public void testPersist() {
        makeStickyNote();
    }

    @Test
    public void testFind() {
        List<StickyNote> list = service.find(null);
        assertEquals(1, list.size());
        verify(list.get(0));
    }

    @Test
    public void testDelete() {
        StickyNote stickyNote = makeStickyNote();
        service.delete(stickyNote.getId());
        StickyNote removed = service.getById(stickyNote.getId());
        assertNull(removed);
    }

    @Test
    public void testDeleteMany() {
        StickyNote stickyNote = makeStickyNote();
        StickyNote stickyNote2 = makeStickyNote();
        StickyNote stickyNote3 = makeStickyNote();
        service.deleteMany(new int[]{stickyNote.getId(), stickyNote2.getId(), stickyNote3.getId()});
        StickyNote removed = service.getById(stickyNote.getId());
        assertNull(removed);
        removed = service.getById(stickyNote2.getId());
        assertNull(removed);
        removed = service.getById(stickyNote3.getId());
        assertNull(removed);
    }

    /**
     * Creates an StickyNote. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @return  The StickyNote.
     */
    private StickyNote makeStickyNote() {
        StickyNote stickyNote = new StickyNote();
        stickyNote.setHeadline("test headline");
        stickyNote.setNote("test note");

        service.persist(stickyNote);
        return stickyNote;
    }

    /**
     * Verifies the test StickyNote.
     *
     * @param   stickyNote The test StickyNote.
     */
    private void verify(StickyNote stickyNote) {
        assertEquals("test headline", stickyNote.getHeadline());
        assertEquals("test note", stickyNote.getNote());
    }
}