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

import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import net.java.beans.BeanException;
import net.java.convert.ConversionException;

/**
 * This class contains all the tests for the JavaBean class.
 * This should not directly test any other class.
 *
 * @author  Brian Pontarelli
 */
public class ExpressionEvaluatorTest {
    /**
     * Tests getting of local bean properties
     */
    @Test
    public void testLocalGetting() {
        // Test local property null
        Action action = new Action();
        assertNull(ExpressionEvaluator.getValue("user", action));

        // Test local property get
        action = new Action();
        User user = new User();
        action.setUser(user);
        assertSame(action, ExpressionEvaluator.getValue("user", user));
    }

    /**
     * Tests getting of nested bean properties
     */
    @Test
    public void testNestedGetting() {
        Action action = new Action();
        assertNull(ExpressionEvaluator.getValue("property1.property2.property3", bean));

        bean = new Action();
        bean.setBean2(new Action());
        bean.getBean2().setBean3(new User());
        bean.getBean2().getBean3().setName("foo");
        Object obj = ExpressionEvaluator.getValue("property1.property2", bean);
        assertTrue("Should be a User instance prepopulated ", obj instanceof User);
        assertEquals("foo", ExpressionEvaluator.getValue("property1.property2.property3", bean));
    }

    /**
     * Tests the setting of nested bean properties
     */
    @Test
    public void testLocalSetting() {
        // Test local set null
        Action action = new Action();
        bean.setBean2(new Action());
        ExpressionEvaluator.setValue("property1", bean, null);
        assertNull(bean.getBean2());

        // Test local set success
        bean = new Action();
        Action action = new Action();
        ExpressionEvaluator.setValue("property1", bean, action);
        assertSame(action, bean.getBean2());
    }

    /**
     * Tests the setting of nested bean properties
     */
    @Test
    public void testNestedSetting() {
        // Test nested set failure
        Action action = new Action();
        ExpressionEvaluator.setValue("property1.property2.property3", bean, "foo");
        assertEquals("foo", bean.getBean2().getBean3().getName());

        bean = new Action();
        ExpressionEvaluator.setValue("property1", bean, new Action());
        assertNotNull("Should have a bean2", bean.getBean2());
        assertNull("Should not have a bean3", bean.getBean2().getBean3());
    }

    /**
     * Tests the getting of indexed properties
     */
    @Test
    public void testGettingIndexed() {
        // Test a simple local property
        Action action = new Action();
        Action action = new Action();
        bean.setIndexed(0, action);
        assertSame(action, ExpressionEvaluator.getValue("indexed[0]", bean));

        // Test a indexed property
        bean = new Action();
        bean.setIndexed(0, new Action());
        bean.getIndexed(0).setName("foo");
        assertEquals("foo", ExpressionEvaluator.getValue("indexed[0].name", bean));

        bean = new Action();
        bean.setIndexed(0, new Action());
        bean.getIndexed(0).setName("foo");
        assertNull(ExpressionEvaluator.getValue("indexed[1].name", bean));

        bean = new Action();
        bean.setIndexed(0, new Action());
        bean.getIndexed(0).setBean3(new User());
        bean.getIndexed(0).getBean3().setName("foo");
        assertEquals("foo", ExpressionEvaluator.getValue("indexed[0].property2.property3", bean));
    }

    /**
     * Tests the setting of indexed properties
     */
    @Test
    public void testSettingIndexed() {
        // Test a local set
        Action action = new Action();
        Action action = new Action();
        ExpressionEvaluator.setValue("indexed[0]", bean, action);
        assertSame(action, bean.getIndexed(0));

        // Test a nested indexed set
        bean = new Action();
        ExpressionEvaluator.setValue("indexed[0].name", bean, "foo");
        assertEquals("foo", bean.getIndexed(0).getName());

        // Test conversions
        bean = new Action();
        ExpressionEvaluator.setValue("indexed[0].property2.integer3", bean, "16");
        assertEquals(16, (int) bean.getIndexed(0).getBean3().getAge());
    }

    /**
     * Tests single array retrieval locally and nested
     */
    @Test
    public void testSingleArrayGet() {
        Action Action = new Action();
        Action action = new Action();
        Action.getSingleArray()[0] = action;
        assertSame(action, ExpressionEvaluator.getValue("singleArray[0]", Action));

        Action = new Action();
        action = new Action();
        Action.getSingleArray()[0] = action;
        action.setName("fred");
        assertEquals("fred", ExpressionEvaluator.getValue("singleArray[0].name", Action));

        Action = new Action();
        assertNull(ExpressionEvaluator.getValue("singleArray[0].name", Action));

        Action = new Action();
        action = new Action();
        Action.getSingleArray()[0] = action;
        assertNull(ExpressionEvaluator.getValue("singleArray[0].name", Action));

        Action = new Action();
        action = new Action();
        Action.getSingleArray()[0] = action;
        action.setName("Fred");
        assertEquals("Fred", ExpressionEvaluator.getValue("singleArray[0].name", Action));
    }

    /**
     * Tests single array setting locally and nested
     */
    @Test
    public void testSingleArraySet() {
        Action Action = new Action();
        Action action = new Action();
        ExpressionEvaluator.setValue("singleArray[0]", Action, action);
        assertSame(action, Action.getSingleArray()[0]);

        Action = new Action();
        action = new Action();
        Action.getSingleArray()[0] = action;
        ExpressionEvaluator.setValue("singleArray[0].name", Action, "fred");
        assertEquals("fred", Action.getSingleArray()[0].getName());

        // Test creation of the array
        Action = new Action();
        Action.setSingleArray(null);
        ExpressionEvaluator.setValue("singleArray[2].name", Action, "fred");
        assertEquals(3, Action.getSingleArray().length);
        assertNotNull(Action.getSingleArray()[2]);
        assertEquals("fred", Action.getSingleArray()[2].getName());

        // Test creation of the array using Object
        Action = new Action();
        action = new Action();
        Action.setSingleArrayObject(null);
        ExpressionEvaluator.setValue("singleArrayObject[2]", Action, action);
        assertEquals(3, Action.getSingleArrayObject().length);
        assertNotNull(Action.getSingleArrayObject()[2]);
        assertTrue(Action.getSingleArrayObject()[2] instanceof Action);
        assertSame(action, Action.getSingleArrayObject()[2]);

        // Test Object failure
        try {
            Action = new Action();
            action = new Action();
            Action.setSingleArrayObject(null);
            ExpressionEvaluator.setValue("singleArrayObject[2].name", Action, action);
            fail("Should have failed");
        } catch (Exception e) {
            // expected
        }
    }

    /**
     * Tests multi array retrieval locally and nested
     */
    @Test
    public void testMultiArrayGet() {
        Action Action = new Action();
        Action action = new Action();
        Action.setMultiArray(new Action[1][2][3]);
        Action.getMultiArray()[0][1][2] = action;
        assertSame(action, ExpressionEvaluator.getValue("multiArray[0][1][2]", Action));

        Action = new Action();
        action = new Action();
        Action.setMultiArray(new Action[1][2][5]);
        Action.getMultiArray()[0][1][4] = action;
        action.setName("fred");
        assertEquals("fred", ExpressionEvaluator.getValue("multiArray[0][1][4].name", Action));

        Action = new Action();
        assertNull(ExpressionEvaluator.getValue("multiArray[0][1][1].name", Action));

        Action = new Action();
        action = new Action();
        Action.setMultiArray(new Action[3][1][6]);
        Action.getMultiArray()[2][0][5] = action;
        assertNull(ExpressionEvaluator.getValue("multiArray[2][0][5].name", Action));
    }

    /**
     * Tests single array setting locally and nested
     */
    @Test
    public void testMultiArraySet() {
        Action Action = new Action();
        Action action = new Action();
        Action.setMultiArray(new Action[1][4][2]);
        ExpressionEvaluator.setValue("multiArray[0][3][1]", Action, action);
        assertSame(action, Action.getMultiArray()[0][3][1]);

        Action = new Action();
        action = new Action();
        Action.setMultiArray(new Action[1][3][5]);
        Action.getMultiArray()[0][2][4] = action;
        ExpressionEvaluator.setValue("multiArray[0][2][4].name", Action, "fred");
        assertEquals("fred", Action.getMultiArray()[0][2][4].getName());

        // Test creation of the array
        Action = new Action();
        Action.setMultiArray(null);
        ExpressionEvaluator.setValue("multiArray[2][0][3].name", Action, "fred");
        assertEquals(3, Action.getMultiArray().length);
        assertNotNull(Action.getMultiArray()[2][0][3]);
        assertEquals("fred", Action.getMultiArray()[2][0][3].getName());

        // Test Object multi array
        Action = new Action();
        action = new Action();
        Action.setMultiArrayObject(null);
        ExpressionEvaluator.setValue("multiArrayObject[2][3][1]", Action, action);
        assertEquals(3, Action.getMultiArrayObject().length);
        assertNotNull(Action.getMultiArrayObject()[2][3][1]);
        assertSame(action, Action.getMultiArrayObject()[2][3][1]);

        // Test Object multi array
        try{
            Action = new Action();
            action = new Action();
            Action.setMultiArrayObject(null);
            ExpressionEvaluator.setValue("multiArrayObject[2][3]", Action, action);
            fail("should have failed because not correct type");
        } catch (BeanException be) {
            fail("Should be a type conversion exception");
        } catch (ConversionException tce) {
            // Expected
        }
    }

    /**
     * Tests Collection usage
     */
    @Test
    public void testCollection() {
        User user = new User();
        Action action = new Action();
        user.getCollection().add(action);
        action.setName("fred");
        try {
            assertEquals("fred", ExpressionEvaluator.getValue("collection[0].name", user));
            fail("Non-generic collection should fail.");
        } catch (Exception e) {
            // Expected
        }
    }

    /**
     * Tests Map usage
     */
    @Test
    public void testMap() {
        User user = new User();
        Action action = new Action();
        user.getMap().put("b2", action);
        action.setName("fred");
        try {
            assertNotNull("Should have Action", ExpressionEvaluator.getValue("map[b2]", user));
            fail("Non-generic map should fail.");
        } catch (Exception e) {
            // expected
        }

        Action Action = new Action();
        ExpressionEvaluator.setValue("genericMap['home'].name", Action, "fred");
        assertEquals("fred", Action.getGenericMap().get("home").getName());
        assertEquals("fred", ExpressionEvaluator.getValue("genericMap['home'].name", Action));

        Action = new Action();
        ExpressionEvaluator.setValue("genericMapIndexed['home'].name", Action, "fred");
        assertEquals("fred", Action.getGenericMapIndexed("home").getName());
        assertEquals("fred", ExpressionEvaluator.getValue("genericMapIndexed['home'].name", Action));
    }
}