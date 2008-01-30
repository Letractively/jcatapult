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
package org.jcatapult.filemgr.action;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.filemgr.service.FileManagerServiceImpl;
import org.jcatapult.filemgr.service.FileConfigurationImpl;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.junit.Assert;
import org.junit.Test;

import net.java.io.FileTools;

/**
 * <p>
 * This tests the FCKFileManager action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FckFileManagerTest {
    @Test
    public void testContentTypeFail() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FckFileManager fm = new FckFileManager(new FileManagerServiceImpl(new FileConfigurationImpl(configuration),
            servletContext));
        fm.setCommand(FileManagerCommand.FileUpload);
        fm.setNewFileContentType("application/active-x");
        String result = fm.execute();
        Assert.assertEquals("success", result);
        Assert.assertEquals(1, fm.getConnector().getError().getNumber());

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testRelativeSuccess() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        FckFileManager fm = new FckFileManager(new FileManagerServiceImpl(new FileConfigurationImpl(configuration),
            servletContext));
        fm.setCommand(FileManagerCommand.FileUpload);
        fm.setNewFileContentType("image/gif");
        fm.setNewFileFileName("foo-bar.xml");

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);
        fm.setNewFile(temp);

        String result = fm.execute();
        Assert.assertEquals("success", result);

        File check = new File("/tmp/jcatapult-filemgr/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        Assert.assertEquals("/foo/some-dir/foo-bar.xml", fm.getConnector().getUploadResult().getFileURL());
        Assert.assertEquals(0, fm.getConnector().getUploadResult().getResultCode());

        EasyMock.verify(httpRequest, servletContext, configuration);
    }
}