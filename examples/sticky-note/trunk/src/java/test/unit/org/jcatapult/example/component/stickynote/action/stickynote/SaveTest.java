package org.jcatapult.example.component.stickynote.action.stickynote;

import static junit.framework.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.example.component.stickynote.domain.StickyNote;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;

/**
 * <p>
 * This class tests the save action.
 * </p>
 *
 * @author  Scaffolder
 */
public class SaveTest {
    /**
     * Tests save.
     */
    @Test
    public void testSave() {
        StickyNote stickyNote = new StickyNote();
        StickyNoteService service = EasyMock.createStrictMock(StickyNoteService.class);
        service.persist(stickyNote);
        EasyMock.replay(service);

        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.replay(configuration);

        Save save = new Save(service, configuration);
        save.setStickyNote(stickyNote);
        String result = save.execute();
        assertEquals("success", result);
        EasyMock.verify(service, configuration);
    }
}