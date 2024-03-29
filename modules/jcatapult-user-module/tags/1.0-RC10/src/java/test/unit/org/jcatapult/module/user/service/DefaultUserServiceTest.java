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
package org.jcatapult.module.user.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.Name;
import org.jcatapult.module.user.domain.PhoneNumber;
import org.jcatapult.module.user.domain.Role;
import org.jcatapult.module.user.domain.User;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class DefaultUserServiceTest extends BaseTest {
    private UserService service;

    @Inject
    public void setService(UserService service) {
        this.service = service;
    }

    @Test
    public void testPersist() throws SQLException {
        clear();

        makeUser("test login");
    }

    @Test
    public void testRegister() throws Exception {
        DefaultUser user = new DefaultUser();
        user.setCompanyName("Jcatapult");
        user.setName(new Name());
        user.getName().setFirstName("Brian");
        user.getName().setLastName("Pontarelli");
        user.setLogin("foo@bar.com");
        user.setPassword("password");
        assertEquals(RegisterResult.SUCCESS, service.register(user, "p"));
        assertFalse(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());
    }

    @Test
    public void testRegisterDuplicate() throws Exception {
        // This user was persisted in testRegister method
        DefaultUser user = new DefaultUser();
        user.setCompanyName("Jcatapult");
        user.setName(new Name());
        user.getName().setFirstName("Brian");
        user.getName().setLastName("Pontarelli");
        user.setLogin("foo@bar.com");
        user.setPassword("password");
        assertEquals(RegisterResult.EXISTS, service.register(user, "p"));
    }

    @Test
    public void testRegisterPartial() throws Exception {
        DefaultUser user = new DefaultUser();
        user.setLogin("partial@bar.com");
        user.setPassword("password");
        assertEquals(RegisterResult.SUCCESS, service.registerPartial(user));
        assertTrue(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());

        user = new DefaultUser();
        user.setCompanyName("Jcatapult");
        user.setName(new Name());
        user.getName().setFirstName("Brian");
        user.getName().setLastName("Pontarelli");
        user.setLogin("partial@bar.com");
        user.setPassword("password");
        assertEquals(RegisterResult.SUCCESS, service.register(user, "password"));
        assertFalse(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());
    }

    @Test
    public void testPersistExisting() throws SQLException {
        clear();
        Map<String, Integer[]> associations = new HashMap<String, Integer[]>();
        associations.put("roles", new Integer[]{1, 2});

        DefaultUser user = makeUser("test login");
        String password = user.getPassword();
        service.persist(user, associations, "new");
        assertFalse(user.getPassword().equals(password));
    }

//    @Test
    public void testUpdate() throws SQLException {
        clear();

        DefaultUser user = makeUser("test login");
        Integer addressId = user.getAddresses().get("work").getId();
        Integer phoneNumberId = user.getPhoneNumbers().get("work").getId();
        assertNotNull(addressId);
        assertNotNull(phoneNumberId);

        DefaultUser newUserData = new DefaultUser();
        newUserData.setCompanyName("New");
        newUserData.setName(new Name());
        newUserData.getName().setFirstName("New");
        newUserData.getName().setLastName("New");
        newUserData.getAddresses().put("work", new Address("new street", "new city", "new state", null, "new country", "new postal", "work"));
        newUserData.getPhoneNumbers().put("work", new PhoneNumber("303-555-1111", "work"));

        service.update(newUserData, "p");

        persistenceService.reload(user);
        assertNotNull(persistenceService.findById(Address.class, addressId));
        assertNotNull(persistenceService.findById(PhoneNumber.class, phoneNumberId));
        assertEquals(addressId, user.getAddresses().get("work").getId());
        assertEquals(phoneNumberId, user.getPhoneNumbers().get("work").getId());
    }

    @Test
    public void testFind() throws SQLException {
        clear();
        makeUser("test login");

        List<User> list = service.find(null);
        assertEquals(1, list.size());
        verify((DefaultUser) list.get(0));
    }

    @Test
    public void testDelete() throws SQLException {
        clear();

        DefaultUser user = makeUser("test login");
        service.delete(user.getId());
        DefaultUser removed = (DefaultUser) service.findById(user.getId());
        assertTrue(removed.isDeleted());
    }

    @Test
    public void testDeleteMany() throws SQLException {
        clear();

        DefaultUser user = makeUser("test login1");
        DefaultUser user2 = makeUser("test login2");
        DefaultUser user3 = makeUser("test login3");
        service.deleteMany(new int[]{user.getId(), user2.getId(), user3.getId()});
        DefaultUser removed = (DefaultUser) service.findById(user.getId());
        assertTrue(removed.isDeleted());
        removed = (DefaultUser) service.findById(user2.getId());
        assertTrue(removed.isDeleted());
        removed = (DefaultUser) service.findById(user3.getId());
        assertTrue(removed.isDeleted());
    }

    @Test
    public void testRoles() {
        List<Role> roles = (List<Role>) service.getAssociationObjects().get("roles");
        assertNotNull(roles);
        assertTrue(roles.size() > 0);
    }

    /**
     * Creates an User. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @param   login The user login.
     * @return  The User.
     */
    protected DefaultUser makeUser(String login) {
        DefaultUser user = new DefaultUser();
        user.setGuid("test guid");
        user.setLogin(login);
        user.setPassword("test password");
        user.setCompanyName("test company name");
        user.setName(new Name());
        user.getName().setFirstName("test first name");
        user.getName().setLastName("test first name");
        user.getAddresses().put("work", new Address("test street", "test city", "test state", null, "test country", "test postal", "work"));
        user.getPhoneNumbers().put("work", new PhoneNumber("303-555-5555", "work"));

        Map<String, Integer[]> associations = new HashMap<String, Integer[]>();
        associations.put("roles", new Integer[]{1, 2});

        service.persist(user, associations, "p");
        return user;
    }

    /**
     * Verifies the test User.
     *
     * @param   user The test User.
     */
    private void verify(DefaultUser user) {
        assertTrue(user.getRoles().contains(new DefaultRole("admin")));
        assertTrue(user.getRoles().contains(new DefaultRole("user")));
        assertEquals("test guid", user.getGuid());
        assertEquals("test login", user.getLogin());
        assertEquals("XsDTm62xKL8t8SM8N8m3gg==", user.getPassword());
    }
}