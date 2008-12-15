/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.crud.service;

import java.sql.SQLException;
import java.util.List;

import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

import com.inversoft.crud.domain.User;

/**
 * <p>
 * This class tests the search service impl.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultSearchServiceTest extends JPABaseTest {
    private SearchService searchService;
    private PersistenceService persistenceService;

    @Inject
    public void setServices(SearchService searchService, PersistenceService persistenceService) {
        this.searchService = searchService;
        this.persistenceService = persistenceService;
    }

    @Test
    public void testSearchSimple() throws SQLException {
        clearTable("User");
        makeUser("Fred", 42);

        UserSearchCriteria criteria = new UserSearchCriteria();
        List<User> users = searchService.find(criteria);
        assertEquals(1, users.size());
        assertEquals(1, searchService.totalCount(criteria));
    }

    @Test
    public void testName() throws SQLException {
        clearTable("User");
        makeUser("Fred", 42);
        makeUser("George", 32);
        makeUser("Betsy", 29);

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName("Fred");
        List<User> users = searchService.find(criteria);
        assertEquals(1, users.size());
        assertEquals("Fred", users.get(0).getName());
        assertEquals(42, (int) users.get(0).getAge());

        assertEquals(1, searchService.totalCount(criteria));
    }

    @Test
    public void testAge() throws SQLException {
        clearTable("User");
        makeUser("Fred", 42);
        makeUser("George", 32);
        makeUser("Betsy", 29);

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setAge(40);
        List<User> users = searchService.find(criteria);
        assertEquals(2, users.size());
        assertEquals("George", users.get(0).getName());
        assertEquals(32, (int) users.get(0).getAge());
        assertEquals("Betsy", users.get(1).getName());
        assertEquals(29, (int) users.get(1).getAge());

        assertEquals(2, searchService.totalCount(criteria));
    }

    private void makeUser(String name, Integer age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        persistenceService.persist(user);
    }
}