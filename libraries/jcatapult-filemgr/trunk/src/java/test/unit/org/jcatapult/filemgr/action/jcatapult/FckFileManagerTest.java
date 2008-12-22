/*
 * Copyright (c) 2001-2007, Inversoft, All Rights Reserved
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
package org.jcatapult.filemgr.action.jcatapult;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.filemgr.BaseTest;
import org.jcatapult.filemgr.service.DefaultFileConfiguration;
import org.jcatapult.filemgr.service.DefaultFileManagerService;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;
import org.jcatapult.servlet.ServletObjectsHolder;
import static org.junit.Assert.*;
import org.junit.Test;

import net.java.io.FileTools;

/**
 * <p>
 * This tests the FCKFileManager action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FckFileManagerTest extends BaseTest {
    @Test
    public void testContentTypeFail() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FckFileManager fm = new FckFileManager(new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext));
        fm.command = FileManagerCommand.FileUpload;
        fm.newFile = new FileInfo(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), "test-file", "application/active-x");
        String result = fm.execute();
        assertEquals("upload", result);
        assertEquals(1, fm.getConnector().getError().getNumber());

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testRelativeSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        FckFileManager fm = new FckFileManager(new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext));
        fm.command = FileManagerCommand.FileUpload;

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);
        fm.newFile = new FileInfo(temp, "foo-bar.xml", "image/gif");

        String result = fm.execute();
        assertEquals("upload", result);

        System.out.println("" + fm.getConnector().getError());
        assertNull(fm.getConnector().getError());
        assertEquals(0, fm.getConnector().getUploadResult().getResultCode());
        assertEquals("/foo/some-dir/foo-bar.xml", fm.getConnector().getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        assertTrue(check.exists());
        assertTrue(check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }
}