package com.example.actions.ns.bean;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;
import com.example.services.BeanService;

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
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        List<Email> emails = new ArrayList<Email>();
        EasyMock.expect(service.getEmails()).andReturn(emails);
        List<Category> categories = new ArrayList<Category>();
        EasyMock.expect(service.getCategories()).andReturn(categories);
        EasyMock.replay(service);

        Prepare prepare = new Prepare(service);
        String result = prepare.execute();
        assertEquals("success", result);

        assertSame(emails, prepare.getEmails());
        assertSame(categories, prepare.getCategories());

        EasyMock.verify(service);
    }
}