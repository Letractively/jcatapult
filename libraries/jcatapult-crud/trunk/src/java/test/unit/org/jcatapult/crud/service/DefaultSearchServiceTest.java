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
package org.jcatapult.crud.service;

import java.sql.SQLException;
import java.util.List;

import org.jcatapult.crud.BaseTest;
import org.jcatapult.crud.domain.User;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the search service impl.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultSearchServiceTest extends BaseTest {
    @Inject public SearchService searchService;

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
        criteria.name = "Fred";
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
        criteria.age = 40;
        List<User> users = searchService.find(criteria);
        assertEquals(2, users.size());
        assertEquals("George", users.get(0).getName());
        assertEquals(32, (int) users.get(0).getAge());
        assertEquals("Betsy", users.get(1).getName());
        assertEquals(29, (int) users.get(1).getAge());

        assertEquals(2, searchService.totalCount(criteria));
    }
}