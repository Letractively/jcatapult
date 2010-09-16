/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jcatapult.test.servlet.MockHttpServletRequest;
import org.jcatapult.test.servlet.MockServletContext;
import org.junit.Test;

/**
 * <p>
 * This class tests the mocks.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MockHttpServletRequestTest {
    @Test
    public void multipart() throws IOException, FileUploadException {
        MockHttpServletRequest request = new MockHttpServletRequest("/foo", new MockServletContext());
        request.addFile("file", new File("src/java/test/unit/org/jcatapult/servlet/test-file.txt"), "text/plain");
        request.setParameter("test", "test");

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        assertEquals(2, items.size());
        assertTrue(items.get(0).isFormField());
        assertEquals(items.get(0).getFieldName(), "test");
        assertEquals(items.get(0).getString(), "test");

        assertFalse(items.get(1).isFormField());
        assertEquals(items.get(1).getFieldName(), "file");
        assertEquals(items.get(1).getName(), "test-file.txt");
        assertEquals(items.get(1).getContentType(), "text/plain");
    }
}
