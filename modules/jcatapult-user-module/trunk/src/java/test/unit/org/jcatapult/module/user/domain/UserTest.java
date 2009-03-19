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
package org.jcatapult.module.user.domain;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.BaseTest;

/**
 * <p>
 * This class tests the User entity.
 * </p>
 *
 * @author James Humphrey
 */
public class UserTest extends BaseTest {
    @Test
    public void testDuplicates() {
        makeUser("test-dups@test.com");
        DefaultRole role = persistenceService.findById(DefaultRole.class, 1);
        DefaultUser user = new DefaultUser();
        user.setUsername("test-dups@test.com");
        user.setEmail("test-dups@test.com");
        user.setGuid("test uid");
        user.setPassword("blah blah");
        user.addRole(role);
        try {
            persistenceService.persist(user);
            fail("Should have failed");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testContactInfo() {
        DefaultRole role = persistenceService.findById(DefaultRole.class, 1);
        DefaultUser user = new DefaultUser();
        user.setUsername("test-insert@test.com");
        user.setEmail("test-insert@test.com");
        user.setGuid("test uid");
        user.setPassword("blah blah");
        user.addRole(role);
        user.setCompanyName("Company");
        user.addEmailAddress(new EmailAddress("test-insert@test.com", "home"));
        user.setName(new Name());
        user.getName().setFirstName("Brian");
        user.getName().setLastName("Pontarelli");

        Address address = new Address("13275 Elk Mountain Way", "Broomfield", "Colorado", null, "US", "80020", "Home");
        Address workAddress = new Address("1600 Pearl St.", "Boulder", "Colorado", null, "US", "80302", "Work");
        user.addAddress(address);
        user.addAddress(workAddress);

        PhoneNumber home = new PhoneNumber("303-954-0394", "Home");
        PhoneNumber cell = new PhoneNumber("720-352-1193", "Cell");
        user.addPhoneNumber(home);
        user.addPhoneNumber(cell);

        persistenceService.persist(user);

        persistenceService.clearCache();

        user = persistenceService.queryFirst(DefaultUser.class, "select u from DefaultUser u where u.username = ?1", "test-insert@test.com");
        Assert.assertEquals("test uid", user.getGuid());
        Assert.assertEquals("blah blah", user.getPassword());
        Assert.assertEquals(1, user.getRoles().size());
        Assert.assertEquals("user", user.getRoles().iterator().next().getName());
        Assert.assertFalse(user.isDeleted());
        Assert.assertFalse(user.isAdmin());
        Assert.assertFalse(user.isExpired());
        Assert.assertFalse(user.isLocked());
        Assert.assertFalse(user.isPasswordExpired());
        Assert.assertEquals("Company", user.getCompanyName());
        Assert.assertEquals(new EmailAddress("test-insert@test.com", "home"), user.getEmailAddresses().get("home"));
        Assert.assertEquals("Brian", user.getName().getFirstName());
        Assert.assertEquals("Pontarelli", user.getName().getLastName());
        Assert.assertEquals("Broomfield", user.getAddresses().get("Home").getCity());
        Assert.assertEquals("US", user.getAddresses().get("Home").getCountry());
        Assert.assertEquals("80020", user.getAddresses().get("Home").getPostalCode());
        Assert.assertEquals("Colorado", user.getAddresses().get("Home").getState());
        Assert.assertEquals("13275 Elk Mountain Way", user.getAddresses().get("Home").getStreet());
        Assert.assertEquals("Home", user.getAddresses().get("Home").getType());

        Assert.assertEquals("Boulder", user.getAddresses().get("Work").getCity());
        Assert.assertEquals("US", user.getAddresses().get("Work").getCountry());
        Assert.assertEquals("80302", user.getAddresses().get("Work").getPostalCode());
        Assert.assertEquals("Colorado", user.getAddresses().get("Work").getState());
        Assert.assertEquals("1600 Pearl St.", user.getAddresses().get("Work").getStreet());
        Assert.assertEquals("Work", user.getAddresses().get("Work").getType());

        Assert.assertEquals("303-954-0394", user.getPhoneNumbers().get("Home").getNumber());
        Assert.assertEquals("Home", user.getPhoneNumbers().get("Home").getType());

        Assert.assertEquals("720-352-1193", user.getPhoneNumbers().get("Cell").getNumber());
        Assert.assertEquals("Cell", user.getPhoneNumbers().get("Cell").getType());
    }
}