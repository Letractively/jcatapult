/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package com.texturemedia.catapult.scaffolding;

import java.util.Set;

import org.junit.Test;

import com.texturemedia.catapult.scaffolding.lang.ClassType;
import com.texturemedia.catapult.scaffolding.lang.Field;

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
        ClassType type = ScaffoldingHelper.getType("com.texturemedia.catapult.scaffolding.Bean");
        Set<Field> fields = type.getAllFields();
        for (Field field : fields) {
            System.out.println("Field name " + field.getName());
            System.out.println("Field type " + field.getMainType().getName());
        }
    }
}