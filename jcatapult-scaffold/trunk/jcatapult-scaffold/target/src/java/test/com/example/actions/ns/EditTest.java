package com.example.actions.ns.bean;

import java.util.Arrays;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.scaffold.Bean;
import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;
import com.example.services.BeanService;

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
        BeanService service = EasyMock.createStrictMock(BeanService.class);
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
        Bean bean = new Bean();

        Email email = new Email();
        email.setId(1);
        bean.setEmail(email);
        for (int i = 0; i < 3; i++) {
            Category categories = new Category();
            categories.setId(i + 1);
            bean.getCategories().add(categories);
        }

        BeanService service = EasyMock.createStrictMock(BeanService.class);
        EasyMock.expect(service.getById(1)).andReturn(bean);
        EasyMock.replay(service);

        Edit edit = new Edit(service);
        edit.setId(1);
        String result = edit.execute();
        assertEquals("success", result);
        assertEquals(0, edit.getActionErrors().size());

        assertEquals(1, (int) edit.getEmailId());
        assertTrue(Arrays.equals(new Integer[]{1, 2, 3}, edit.getCategoriesIds()));

        EasyMock.verify(service);
    }
}