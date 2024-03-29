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
 */
package org.jcatapult.mvc.parameter.convert.converters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jcatapult.mvc.parameter.convert.GlobalConverter;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the file converter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileConverterTest {
    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testFromStrings() {
        Map<String, String> map = new HashMap<String, String>();
        GlobalConverter converter = new FileConverter();
        File f = (File) converter.convertFromStrings(array((String) null), File.class, map, "testExpr");
        assertNull(f);

        f = (File) converter.convertFromStrings(array(""), File.class, map, "testExpr");
        assertNull(f);

        f = (File) converter.convertFromStrings(array("/tmp"), File.class, map, "testExpr");
        assertEquals("/tmp", f.getAbsolutePath());

        f = (File) converter.convertFromStrings(array("/tmp", "jcatapult"), File.class, map, "testExpr");
        assertEquals("/tmp/jcatapult", f.getAbsolutePath());

        f = (File) converter.convertFromStrings(array("project.xml"), File.class, map, "testExpr");
        assertEquals(new File("project.xml").getAbsolutePath(), f.getAbsolutePath());

        File[] fa = (File[]) converter.convertFromStrings(array("project.xml", "build.xml"), File[].class, map, "testExpr");
        assertEquals(new File("project.xml").getAbsolutePath(), fa[0].getAbsolutePath());
        assertEquals(new File("build.xml").getAbsolutePath(), fa[1].getAbsolutePath());

        // Test parentDir
        map.put("parentDir", "/tmp");
        f = (File) converter.convertFromStrings(array("project.xml"), File.class, map, "testExpr");
        assertEquals(new File("/tmp/project.xml").getAbsolutePath(), f.getAbsolutePath());
    }

    /**
     * Test the conversion from Strings.
     */
    @Test
    public void testToStrings() {
        GlobalConverter converter = new FileConverter();
        String str = converter.convertToString(null, File.class, null, "testExpr");
        assertNull(str);

        str = converter.convertToString(new File("project.xml"), File.class, null, "testExpr");
        assertEquals(new File("project.xml").getAbsolutePath(), str);
    }
}