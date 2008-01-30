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
package org.jcatapult.filemgr.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.domain.Folder;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.junit.Assert;
import org.junit.Test;

import net.java.io.FileTools;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileManagerServiceImplTest {
    @Test
    public void testContentTypeFail() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.upload(null, null, "application/active-x", null);
        Assert.assertTrue(connector.isError());
        Assert.assertNotNull(connector.getError().getMessage());
        Assert.assertEquals(1, connector.getError().getNumber());

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testMissingDirName() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        try {
            service.upload(null, null, "image/gif", null);
            Assert.fail("Should have failed");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("file-mgr.file-servlet.dir"));
        }

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testRelativeFailure() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn(null);
        EasyMock.replay(servletContext);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        try {
            service.upload(null, null, "image/gif", null);
            Assert.fail("Should have failed");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("file-mgr.file-servlet.dir"));
            Assert.assertTrue(e.getMessage().contains("some-dir"));
        }

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testRelativeSuccess() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", null);
        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/foo/some-dir/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File("/tmp/jcatapult-filemgr/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testRelativeSuccessWithType() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir/image")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", "image");
        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/foo/some-dir/image/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File("/tmp/jcatapult-filemgr/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testAbsoluteFailure() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir").times(2);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir"); // Exception message
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        try {
            service.upload(temp, "foo-bar.xml", "image/gif", null);
            Assert.fail("Should have failed");
        } catch (Exception e) {
        }
    }

    @Test
    public void testAbsoluteSuccess() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir").times(2);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix")).andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", null);

        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/file-mgr.file-servlet/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File("/tmp/jcatapult-filemgr/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testAbsoluteSuccessPrefix() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir").times(2);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix")).andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", "image");

        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/file-mgr.file-servlet/image/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File("/tmp/jcatapult-filemgr/some-dir/image", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testAbsoluteSuccessExistingFile() throws IOException {
        testAbsoluteSuccessPrefix();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir").times(2);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix")).andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", "image");

        Assert.assertFalse(connector.isError());
        Assert.assertEquals(201, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/file-mgr.file-servlet/image/foo-bar(1).xml", connector.getUploadResult().getFileURL());

        File check = new File("/tmp/jcatapult-filemgr/some-dir/image", "foo-bar(1).xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testCreateDir() {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix")).andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getBoolean("file-mgr.create-folder.allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.createFolder("", "test", null);
        Assert.assertEquals("CreateFolder", connector.getCommand());
        Assert.assertEquals("/file-mgr.file-servlet/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("", connector.getCurrentFolder().getPath());

        File dir = new File("/tmp/jcatapult-filemgr/some-dir/test");
        Assert.assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void testCreateDirNestedWithType() {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix")).andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getBoolean("file-mgr.create-folder.allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.createFolder("/deep/dir/", "test", "Images");
        Assert.assertEquals("CreateFolder", connector.getCommand());
        Assert.assertEquals("/file-mgr.file-servlet/Images/deep/dir/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("/deep/dir/", connector.getCurrentFolder().getPath());

        File dir = new File("/tmp/jcatapult-filemgr/some-dir/Images/deep/dir/test");
        Assert.assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void testGetFolders() {
        FileTools.prune("/tmp/jcatapult-filemgr");
        new File("/tmp/jcatapult-filemgr/some-dir/test").mkdirs();
        new File("/tmp/jcatapult-filemgr/some-dir/test2").mkdirs();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.getFolders("", null);
        Assert.assertEquals("GetFolders", connector.getCommand());
        Assert.assertEquals("/servlet-context/files/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("", connector.getCurrentFolder().getPath());

        List<Folder> folders = connector.getFolders();
        Assert.assertEquals(2, folders.size());
        Assert.assertEquals("test2", folders.get(0).getName());
        Assert.assertEquals("test", folders.get(1).getName());

        Assert.assertEquals(0, connector.getFiles().size());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void testGetFoldersAndFiles() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        new File("/tmp/jcatapult-filemgr/some-dir/test").mkdirs();
        new File("/tmp/jcatapult-filemgr/some-dir/test2").mkdirs();
        new File("/tmp/jcatapult-filemgr/some-dir/file").createNewFile();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir")).andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        FileManagerServiceImpl service = new FileManagerServiceImpl(configuration, servletContext);
        Connector connector = service.getFoldersAndFiles("", null);
        Assert.assertEquals("GetFoldersAndFiles", connector.getCommand());
        Assert.assertEquals("/servlet-context/files/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("", connector.getCurrentFolder().getPath());

        List<Folder> folders = connector.getFolders();
        Assert.assertEquals(2, folders.size());
        Assert.assertEquals("test2", folders.get(0).getName());
        Assert.assertEquals("test", folders.get(1).getName());

        List<org.jcatapult.filemgr.domain.File> files = connector.getFiles();
        Assert.assertEquals(1, files.size());
        Assert.assertEquals("file", files.get(0).getName());
        Assert.assertEquals(0, files.get(0).getSize());

        EasyMock.verify(configuration, servletContext);
    }
}