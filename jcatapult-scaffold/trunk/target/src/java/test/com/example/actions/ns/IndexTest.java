package com.example.actions.ns.bean;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import org.jcatapult.scaffold.Bean;
import com.example.services.BeanService;

/**
 * <p>
 * This class tests the index action.
 * </p>
 *
 * @author  Scaffolder
 */
public class IndexTest {
    /**
     * Tests index with no sort.
     */
    @Test
    public void testNoSort() {
        List<Bean> findResult = new ArrayList<Bean>();
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        EasyMock.expect(service.find(1, 20, null)).andReturn(findResult);
        EasyMock.expect(service.getNumberOfBeans()).andReturn(0);
        EasyMock.replay(service);

        Index index = new Index(service);
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.getTotalCount());
        assertEquals(1, index.getPage());
        assertEquals(20, index.getNumberPerPage());
        assertSame(findResult, index.getBeans());
        EasyMock.verify(service);
    }

    /**
     * Tests index with sort.
     */
    @Test
    public void testSort() {
        List<Bean> findResult = new ArrayList<Bean>();
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        EasyMock.expect(service.find(1, 20, "test")).andReturn(findResult);
        EasyMock.expect(service.getNumberOfBeans()).andReturn(0);
        EasyMock.replay(service);

        Index index = new Index(service);
        index.setSortProperty("test");
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.getTotalCount());
        assertEquals(1, index.getPage());
        assertEquals(20, index.getNumberPerPage());
        assertSame(findResult, index.getBeans());
        EasyMock.verify(service);
    }

    /**
     * Tests index with page and number.
     */
    @Test
    public void testPageAndNumber() {
        List<Bean> findResult = new ArrayList<Bean>();
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        EasyMock.expect(service.find(2, 50, "test")).andReturn(findResult);
        EasyMock.expect(service.getNumberOfBeans()).andReturn(100);
        EasyMock.replay(service);

        Index index = new Index(service);
        index.setSortProperty("test");
        index.setPage(2);
        index.setNumberPerPage(50);
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(100, index.getTotalCount());
        assertEquals(2, index.getPage());
        assertEquals(50, index.getNumberPerPage());
        assertSame(findResult, index.getBeans());
        EasyMock.verify(service);
    }

    /**
     * Tests index with showAll.
     */
    @Test
    public void testShowAll() {
        List<Bean> findResult = new ArrayList<Bean>();
        BeanService service = EasyMock.createStrictMock(BeanService.class);
        EasyMock.expect(service.find("test")).andReturn(findResult);
        EasyMock.expect(service.getNumberOfBeans()).andReturn(0);
        EasyMock.replay(service);

        Index index = new Index(service);
        index.setSortProperty("test");
        index.setShowAll(true);
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.getTotalCount());
        assertEquals(1, index.getPage());
        assertEquals(20, index.getNumberPerPage());
        assertSame(findResult, index.getBeans());
        EasyMock.verify(service);
    }
}