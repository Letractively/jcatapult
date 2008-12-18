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
import org.jcatapult.filemgr.BaseTest;
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
public class DefaultFileManagerServiceTest extends BaseTest {
    @Test
    public void testContentTypeFail() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.upload(new File("project.xml"), "foo.xml", "application/active-x", null, null);
        Assert.assertTrue(connector.isError());
        Assert.assertNotNull(connector.getError().getMessage());
        Assert.assertEquals(1, connector.getError().getNumber());

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testMissingDirName() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        try {
            service.upload(new File("project.xml"), "foo.xml", "image/gif", null, null);
            Assert.fail("Should have failed");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("file-mgr.file-servlet.dir"));
        }

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void testRelativeFailure() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn(null);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        try {
            service.upload(new File("project.xml"), "foo.xml", "image/gif", null, null);
            Assert.fail("Should have failed");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("file-mgr.file-servlet.dir"));
            Assert.assertTrue(e.getMessage().contains("some-dir"));
        }

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
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", null, "");
        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/foo/some-dir/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testRelativeSuccessWithType() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir/image")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", "image", "");
        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/foo/some-dir/image/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testAbsoluteFailure() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir"); // Exception message
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        try {
            service.upload(temp, "foo-bar.xml", "image/gif", null, "");
            Assert.fail("Should have failed");
        } catch (Exception e) {
        }
    }

    @Test
    public void testAbsoluteSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).
            andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration), servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", null, "");

        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/file-mgr.file-servlet/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testAbsoluteSuccessPrefix() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).
            andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", "image", "");

        Assert.assertFalse(connector.isError());
        Assert.assertEquals(0, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/file-mgr.file-servlet/image/foo-bar.xml", connector.getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir/image", "foo-bar.xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testAbsoluteSuccessExistingFile() throws IOException {
        testAbsoluteSuccessPrefix();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).
            andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.upload(temp, "foo-bar.xml", "image/gif", "image", "");

        Assert.assertFalse(connector.isError());
        Assert.assertEquals(201, connector.getUploadResult().getResultCode());
        Assert.assertEquals("/file-mgr.file-servlet/image/foo-bar(1).xml", connector.getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir/image", "foo-bar(1).xml");
        Assert.assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        Assert.assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testCreateDir() {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).
            andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.createFolder("", "test", null);
        Assert.assertEquals("CreateFolder", connector.getCommand());
        Assert.assertEquals("/file-mgr.file-servlet/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("", connector.getCurrentFolder().getPath());

        File dir = new File(testDir + "/some-dir/test");
        Assert.assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void testCreateDirNestedWithType() {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).
            andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.createFolder("/deep/dir/", "test", "Images");
        Assert.assertEquals("CreateFolder", connector.getCommand());
        Assert.assertEquals("/file-mgr.file-servlet/Images/deep/dir/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("/deep/dir/", connector.getCurrentFolder().getPath());

        File dir = new File(testDir + "/some-dir/Images/deep/dir/test");
        Assert.assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void testGetFolders() {
        FileTools.prune(testDir);
        new File(testDir + "/some-dir/test").mkdirs();
        new File(testDir + "/some-dir/test2").mkdirs();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.getFolders("", null);
        Assert.assertEquals("GetFolders", connector.getCommand());
        Assert.assertEquals("/servlet-context/files/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("", connector.getCurrentFolder().getPath());

        List<Folder> folders = connector.getFolders();
        Assert.assertEquals(2, folders.size());
        Assert.assertTrue(folders.get(0).getName().equals("test2") || folders.get(0).getName().equals("test"));
        Assert.assertTrue(folders.get(1).getName().equals("test2") || folders.get(1).getName().equals("test"));

        Assert.assertEquals(0, connector.getFiles().size());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void testGetFoldersAndFiles() throws IOException {
        FileTools.prune(testDir);
        new File(testDir + "/some-dir/test").mkdirs();
        new File(testDir + "/some-dir/test2").mkdirs();
        new File(testDir + "/some-dir/file").createNewFile();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
            servletContext);
        Connector connector = service.getFoldersAndFiles("", null);
        Assert.assertEquals("GetFoldersAndFiles", connector.getCommand());
        Assert.assertEquals("/servlet-context/files/", connector.getCurrentFolder().getUrl());
        Assert.assertEquals("", connector.getCurrentFolder().getPath());

        List<Folder> folders = connector.getFolders();
        Assert.assertEquals(2, folders.size());
        Assert.assertTrue(folders.get(0).getName().equals("test2") || folders.get(0).getName().equals("test"));
        Assert.assertTrue(folders.get(1).getName().equals("test2") || folders.get(1).getName().equals("test"));

        List<org.jcatapult.filemgr.domain.File> files = connector.getFiles();
        Assert.assertEquals(1, files.size());
        Assert.assertEquals("file", files.get(0).getName());
        Assert.assertEquals(0, files.get(0).getSize());

        EasyMock.verify(configuration, servletContext);
    }
}