package org.jcatapult.example.webapp.action.guestBook;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.example.webapp.service.GuestBookEntryService;

/**
 * <p>
 * This class tests the prepare action.
 * </p>
 *
 * @author  Scaffolder
 */
public class PrepareTest {
    /**
     * Tests prepare.
     */
    @Test
    public void testPrepare() {
        GuestBookEntryService service = EasyMock.createStrictMock(GuestBookEntryService.class);
        EasyMock.replay(service);

        Prepare prepare = new Prepare(service);
        String result = prepare.execute();
        assertEquals("success", result);


        EasyMock.verify(service);
    }
}