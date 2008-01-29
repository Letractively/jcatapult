package com.example.services;

import java.util.List;

import com.texturemedia.catapult.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;
import org.jcatapult.scaffold.Bean;
import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class BeanServiceImplTest extends JPABaseTest {
    private BeanService service;

    @Inject
    public void setService(BeanService service) {
        this.service = service;
    }

    @Test
    public void testPersist() {
        makeBean();
    }

    @Test
    public void testFind() {
        List<Bean> list = service.find(null);
        assertEquals(1, list.size());
        verify(list.get(0));
    }

    @Test
    public void testDelete() {
        Bean bean = makeBean();
        service.delete(bean.getId());
        Bean removed = service.getById(bean.getId());
        assertNull(removed);
    }

    @Test
    public void testDeleteMany() {
        Bean bean = makeBean();
        Bean bean2 = makeBean();
        Bean bean3 = makeBean();
        service.deleteMany(new int[]{bean.getId(), bean2.getId(), bean3.getId()});
        Bean removed = service.getById(bean.getId());
        assertNull(removed);
        removed = service.getById(bean2.getId());
        assertNull(removed);
        removed = service.getById(bean3.getId());
        assertNull(removed);
    }

    @Test
    public void testGetEmails() {
        List<Email> emails = service.getEmails();
        assertNotNull(emails);
        assertTrue(emails.size() > 0);
    }

    @Test
    public void testCategories() {
        List<Category> categories = service.getCategories();
        assertNotNull(categories);
        assertTrue(categories.size() > 0);
    }

    /**
     * Creates an Bean. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @return  The Bean.
     */
    private Bean makeBean() {
        Bean bean = new Bean();
        bean.setName("test name");
        bean.setOptional("test optional");
        Integer[] categoriesIds = new Integer[]{1, 2, 3};

        service.persist(bean, 1, categoriesIds);
        return bean;
    }

    /**
     * Verifies the test Bean.
     *
     * @param   bean The test Bean.
     */
    private void verify(Bean bean) {
        assertEquals(1, (int) bean.getEmail().getId());
        assertEquals(1, (int) bean.getCategories().get(0).getId());
        assertEquals(2, (int) bean.getCategories().get(1).getId());
        assertEquals(3, (int) bean.getCategories().get(2).getId());
        assertEquals("test name", bean.getName());
        assertEquals("test optional", bean.getOptional());
    }
}