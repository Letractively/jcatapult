package org.jcatapult.example.stickynote.action.stickyNotes;

import java.util.Arrays;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.example.stickynote.domain.StickyNote;
import org.jcatapult.example.stickynote.service.StickyNoteService;

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
        StickyNoteService service = EasyMock.createStrictMock(StickyNoteService.class);
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
        StickyNote stickyNote = new StickyNote();


        StickyNoteService service = EasyMock.createStrictMock(StickyNoteService.class);
        EasyMock.expect(service.getById(1)).andReturn(stickyNote);
        EasyMock.replay(service);

        Edit edit = new Edit(service);
        edit.setId(1);
        String result = edit.execute();
        assertEquals("success", result);
        assertEquals(0, edit.getActionErrors().size());


        EasyMock.verify(service);
    }
}