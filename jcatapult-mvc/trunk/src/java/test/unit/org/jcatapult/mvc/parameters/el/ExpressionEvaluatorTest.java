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
package org.jcatapult.mvc.parameters.el;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class contains tests for the expression evaluator.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ExpressionEvaluatorTest {
    /**
     * Tests getting of bean properties
     */
    @Test
    public void testPropertyGetting() {
        // Test local property null
        Action action = new Action();
        assertNull(ExpressionEvaluator.getValue("user", action));

        // Test local property get
        User user = new User();
        action.setUser(user);
        assertSame(user, ExpressionEvaluator.getValue("user", action));

        // Test nested property get
        action.getUser().setAge(32);
        action.getUser().setName("Brian");
        assertEquals(32, ExpressionEvaluator.getValue("user.age", action));
        assertEquals("Brian", ExpressionEvaluator.getValue("user.name", action));

        // Test collection property gets
        Address address = new Address();
        address.setCity("Broomfield");
        address.setState("CO");
        address.setStreet("Test");
        address.setZipcode("80020");
        action.getUser().getAddresses().put("home", address);
        assertNull(ExpressionEvaluator.getValue("user.addresses['work']", action));
        assertEquals("Broomfield", ExpressionEvaluator.getValue("user.addresses['home'].city", action));
        assertEquals("CO", ExpressionEvaluator.getValue("user.addresses['home'].state", action));
        assertEquals("Test", ExpressionEvaluator.getValue("user.addresses['home'].street", action));
        assertEquals("80020", ExpressionEvaluator.getValue("user.addresses['home'].zipcode", action));

        User brother = new User();
        brother.setName("Brett");
        brother.setAge(34);
        user.getSiblings().add(brother);
        assertEquals(34, ExpressionEvaluator.getValue("user.siblings[0].age", action));
        assertEquals("Brett", ExpressionEvaluator.getValue("user.siblings[0].name", action));

        user.setSecurityQuestions(new String[]{"What is your pet's name?", "What is your home town?"});
        assertEquals("What is your pet's name?", ExpressionEvaluator.getValue("user.securityQuestions[0]", action));
        assertEquals("What is your home town?", ExpressionEvaluator.getValue("user.securityQuestions[1]", action));

        // Test indexed collection property gets (using the indexed property methoods)
        assertNull(ExpressionEvaluator.getValue("user.address['work']", action));
        assertEquals("Broomfield", ExpressionEvaluator.getValue("user.address['home'].city", action));
        assertEquals("CO", ExpressionEvaluator.getValue("user.address['home'].state", action));
        assertEquals("Test", ExpressionEvaluator.getValue("user.address['home'].street", action));
        assertEquals("80020", ExpressionEvaluator.getValue("user.address['home'].zipcode", action));

        assertEquals(34, ExpressionEvaluator.getValue("user.sibling[0].age", action));
        assertEquals("Brett", ExpressionEvaluator.getValue("user.sibling[0].name", action));
    }

    /**
     * Tests getting of fields
     */
    @Test
    public void testFieldGetting() {
        // Test local property null
        ActionField action = new ActionField();
        assertNull(ExpressionEvaluator.getValue("user", action));

        // Test local property get
        UserField user = new UserField();
        action.user = user;
        assertSame(user, ExpressionEvaluator.getValue("user", action));

        // Test nested property get
        action.user.age = 32;
        action.user.name = "Brian";
        assertEquals(32, ExpressionEvaluator.getValue("user.age", action));
        assertEquals("Brian", ExpressionEvaluator.getValue("user.name", action));

        // Test collection property gets
        AddressField address = new AddressField();
        address.city = "Broomfield";
        address.state = "CO";
        address.street = "Test";
        address.zipcode = "80020";
        action.user.addresses.put("home", address);
        assertNull(ExpressionEvaluator.getValue("user.addresses['work']", action));
        assertEquals("Broomfield", ExpressionEvaluator.getValue("user.addresses['home'].city", action));
        assertEquals("CO", ExpressionEvaluator.getValue("user.addresses['home'].state", action));
        assertEquals("Test", ExpressionEvaluator.getValue("user.addresses['home'].street", action));
        assertEquals("80020", ExpressionEvaluator.getValue("user.addresses['home'].zipcode", action));

        UserField brother = new UserField();
        brother.name = "Brett";
        brother.age = 34;
        user.siblings.add(brother);
        assertEquals(34, ExpressionEvaluator.getValue("user.siblings[0].age", action));
        assertEquals("Brett", ExpressionEvaluator.getValue("user.siblings[0].name", action));

        user.securityQuestions = new String[]{"What is your pet's name?", "What is your home town?"};
        assertEquals("What is your pet's name?", ExpressionEvaluator.getValue("user.securityQuestions[0]", action));
        assertEquals("What is your home town?", ExpressionEvaluator.getValue("user.securityQuestions[1]", action));
    }
}