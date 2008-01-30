/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffold;

import java.util.Set;

import org.junit.Test;

import org.jcatapult.scaffold.lang.ClassType;
import org.jcatapult.scaffold.lang.Field;

/**
 * <p>
 * This is a test for the scaffolding helper.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ScaffoldingHelperTest {
    @Test
    public void testGetClassInformation() {
        ClassType type = ScaffoldingHelper.getType("org.jcatapult.scaffold.Bean");
        Set<Field> fields = type.getAllFields();
        for (Field field : fields) {
            System.out.println("Field name " + field.getName());
            System.out.println("Field type " + field.getMainType().getName());
        }
    }
}