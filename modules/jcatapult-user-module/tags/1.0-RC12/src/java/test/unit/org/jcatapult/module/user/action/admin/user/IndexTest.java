/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 */
package org.jcatapult.module.user.action.admin.user;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.jcatapult.crud.service.SearchService;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.service.UserSearchCriteria;
import org.jcatapult.test.Capture;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserHandler;
import static org.junit.Assert.*;
import org.junit.Test;

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
        List<User> findResult = new ArrayList<User>();
        SearchService service = EasyMock.createStrictMock(SearchService.class);
        EasyMock.expect(service.find(isA(UserSearchCriteria.class))).andReturn(findResult);
        EasyMock.expect(service.totalCount(isA(UserSearchCriteria.class))).andReturn(0l);
        EasyMock.replay(service);

        UserHandler userHandler = EasyMock.createStrictMock(UserHandler.class);
        EasyMock.expect(userHandler.getUserType()).andReturn(DefaultUser.class);
        EasyMock.replay(userHandler);

        Index index = new Index(userHandler);
        index.setSearchService(service);
        index.prepare();
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.totalCount);
        assertEquals(1, index.searchCriteria.getPage());
        assertEquals(20, index.searchCriteria.getNumberPerPage());
        assertSame(findResult, index.results);
        EasyMock.verify(service, userHandler);
    }

    /**
     * Tests index with sort.
     */
    @Test
    public void testSort() {
        Capture capture = new Capture();
        List<User> findResult = new ArrayList<User>();
        SearchService service = EasyMock.createStrictMock(SearchService.class);
        EasyMock.expect(service.find(capture.<UserSearchCriteria>capture())).andReturn(findResult);
        EasyMock.expect(service.totalCount(isA(UserSearchCriteria.class))).andReturn(0l);
        EasyMock.replay(service);

        UserHandler userHandler = EasyMock.createStrictMock(UserHandler.class);
        EasyMock.expect(userHandler.getUserType()).andReturn(DefaultUser.class);
        EasyMock.replay(userHandler);

        Index index = new Index(userHandler);
        index.setSearchService(service);
        index.prepare();
        index.searchCriteria.setSortProperty("test");
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.totalCount);
        assertEquals(1, index.searchCriteria.getPage());
        assertEquals(20, index.searchCriteria.getNumberPerPage());
        assertSame(findResult, index.results);
        assertEquals("test", ((UserSearchCriteria) capture.object).getSortProperty());
        EasyMock.verify(service, userHandler);
    }

    /**
     * Tests index with page and number.
     */
    @Test
    public void testPageAndNumber() {
        Capture capture = new Capture();
        List<User> findResult = new ArrayList<User>();
        SearchService service = EasyMock.createStrictMock(SearchService.class);
        EasyMock.expect(service.find(capture.<UserSearchCriteria>capture())).andReturn(findResult);
        EasyMock.expect(service.totalCount(isA(UserSearchCriteria.class))).andReturn(0l);
        EasyMock.replay(service);

        UserHandler userHandler = EasyMock.createStrictMock(UserHandler.class);
        EasyMock.expect(userHandler.getUserType()).andReturn(DefaultUser.class);
        EasyMock.replay(userHandler);

        Index index = new Index(userHandler);
        index.setSearchService(service);
        index.prepare();
        index.searchCriteria.setSortProperty("test");
        index.searchCriteria.setNumberPerPage(50);
        index.searchCriteria.setPage(2);
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.totalCount);
        assertEquals(2, index.searchCriteria.getPage());
        assertEquals(50, index.searchCriteria.getNumberPerPage());
        assertSame(findResult, index.results);
        assertEquals("test", ((UserSearchCriteria) capture.object).getSortProperty());
        assertEquals(50, ((UserSearchCriteria) capture.object).getNumberPerPage());
        assertEquals(2, ((UserSearchCriteria) capture.object).getPage());
        EasyMock.verify(service, userHandler);
    }

    /**
     * Tests index with showAll.
     */
    @Test
    public void testShowAll() {
        Capture capture = new Capture();
        List<User> findResult = new ArrayList<User>();
        SearchService service = EasyMock.createStrictMock(SearchService.class);
        EasyMock.expect(service.find(capture.<UserSearchCriteria>capture())).andReturn(findResult);
        EasyMock.expect(service.totalCount(isA(UserSearchCriteria.class))).andReturn(0l);
        EasyMock.replay(service);

        UserHandler userHandler = EasyMock.createStrictMock(UserHandler.class);
        EasyMock.expect(userHandler.getUserType()).andReturn(DefaultUser.class);
        EasyMock.replay(userHandler);

        Index index = new Index(userHandler);
        index.setSearchService(service);
        index.prepare();
        index.searchCriteria.setSortProperty("test");
        index.searchCriteria.setShowAll(true);
        String result = index.execute();
        assertEquals("success", result);
        assertEquals(0, index.totalCount);
        assertEquals(1, index.searchCriteria.getPage());
        assertEquals(20, index.searchCriteria.getNumberPerPage());
        assertSame(findResult, index.results);
        assertEquals("test", ((UserSearchCriteria) capture.object).getSortProperty());
        assertTrue(((UserSearchCriteria) capture.object).isShowAll());
        EasyMock.verify(service, userHandler);
    }
}