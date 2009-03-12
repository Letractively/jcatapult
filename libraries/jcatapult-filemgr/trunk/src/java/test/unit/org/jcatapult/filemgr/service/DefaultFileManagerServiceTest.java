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
import javax.servlet.http.HttpServletRequestWrapper;

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.filemgr.BaseTest;
import org.jcatapult.filemgr.domain.StoreResult;
import org.jcatapult.filemgr.domain.*;
import org.jcatapult.servlet.ServletObjectsHolder;
import static org.junit.Assert.*;
import org.junit.Test;

import net.java.io.FileTools;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultFileManagerServiceTest extends BaseTest {
    @Test
    public void storeMissingDirName() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        try {
            service.store(new File("project.xml"), "foo.xml", "image/gif", null);
            fail("Should have have thrown an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("file-mgr.storage-dir"));
        }

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void storeRelativeFailure() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn(null);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        try {
            service.store(new File("project.xml"), "foo.xml", "image/gif", null);
            fail("Should have failed");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("jcatapult.file-mgr.storage-dir"));
            assertTrue(e.getMessage().contains("some-dir"));
        }

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void storeRelativeSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo");
        EasyMock.replay(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "");
        assertEquals(0, result.getError());
        assertEquals("/foo/some-dir/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void storeRelativeSuccessNested() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir/image")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo");
        EasyMock.replay(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "image");
        assertEquals(0, result.getError());
        assertEquals("/foo/some-dir/image/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void storeAbsoluteFailure() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir"); // Exception message
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        try {
            service.store(temp, "foo-bar.xml", "image/gif", "");
            fail("Should have failed");
        } catch (Exception e) {
        }
    }

    @Test
    public void storeAbsoluteSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "");

        assertEquals(0, result.getError());
        assertEquals("/file-mgr.file-servlet/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/some-dir", "foo-bar.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void storeAbsoluteSuccessPrefix() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "image");

        assertEquals(0, result.getError());
        assertEquals("/file-mgr.file-servlet/image/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/some-dir/image", "foo-bar.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void storeAbsoluteSuccessExistingFile() throws IOException {
        storeAbsoluteSuccessPrefix();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "image");

        assertEquals(0, result.getError());
        assertTrue(result.isChangedFileName());
        assertEquals("/file-mgr.file-servlet/image/foo-bar(1).xml", result.getFileURI());

        File check = new File(testDir + "/some-dir/image", "foo-bar(1).xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void createDir() {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        CreateDirectoryResult result = service.createDirectory("test", "");
        assertEquals("/file-mgr.file-servlet/", result.getURI());
        assertEquals("/", result.getPath());

        File dir = new File(testDir + "/some-dir/test");
        assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void createDirNestedWithType() {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        CreateDirectoryResult result = service.createDirectory("test", "/deep/dir/");
        assertEquals("/file-mgr.file-servlet/deep/dir/", result.getURI());
        assertEquals("/deep/dir/", result.getPath());

        File dir = new File(testDir + "/some-dir/deep/dir/test");
        assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void getFolders() {
        FileTools.prune(testDir);
        new File(testDir + "/some-dir/test").mkdirs();
        new File(testDir + "/some-dir/test2").mkdirs();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context");
        EasyMock.replay(httpRequest);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        Listing listing = service.getFolders("");
        assertEquals("/servlet-context/files/", listing.getURI());
        assertEquals("/", listing.getPath());

        List<DirectoryData> folders = listing.getDirectories();
        assertEquals(2, folders.size());
        assertTrue(folders.get(0).getName().equals("test2") || folders.get(0).getName().equals("test"));
        assertTrue(folders.get(1).getName().equals("test2") || folders.get(1).getName().equals("test"));

        assertEquals(0, listing.getFiles().size());

        EasyMock.verify(configuration, servletContext, httpRequest);
    }

    @Test
    public void getFoldersAndFiles() throws IOException {
        FileTools.prune(testDir);
        new File(testDir + "/some-dir/test").mkdirs();
        new File(testDir + "/some-dir/test2").mkdirs();
        new File(testDir + "/some-dir/file").createNewFile();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn(testDir + "/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context");
        EasyMock.replay(httpRequest);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        Listing listing = service.getFoldersAndFiles("");
        assertEquals("/servlet-context/files/", listing.getURI());
        assertEquals("/", listing.getPath());

        List<DirectoryData> folders = listing.getDirectories();
        assertEquals(2, folders.size());
        assertTrue(folders.get(0).getName().equals("test2") || folders.get(0).getName().equals("test"));
        assertTrue(folders.get(1).getName().equals("test2") || folders.get(1).getName().equals("test"));

        List<FileData> files = listing.getFiles();
        assertEquals(1, files.size());
        assertEquals("file", files.get(0).getName());
        assertEquals(0, files.get(0).getSize());

        EasyMock.verify(configuration, servletContext, httpRequest);
    }

    @Test
    public void getFoldersAndFilesAbsolute() throws IOException {
        FileTools.prune(testDir);
        new File(testDir + "/Files/some-dir").mkdirs();
        new File(testDir + "/Files/some-dir2").mkdirs();
        new File(testDir + "/Files/file").createNewFile();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).andReturn(testDir);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/files");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).andReturn(testDir);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        Listing listing = service.getFoldersAndFiles("Files");
        assertEquals("/files/Files/", listing.getURI());
        assertEquals("/Files/", listing.getPath());

        List<DirectoryData> folders = listing.getDirectories();
        assertEquals(2, folders.size());
        assertTrue(folders.get(0).getName().equals("some-dir") || folders.get(0).getName().equals("some-dir2"));
        assertTrue(folders.get(1).getName().equals("some-dir") || folders.get(1).getName().equals("some-dir2"));

        List<FileData> files = listing.getFiles();
        assertEquals(1, files.size());
        assertEquals("file", files.get(0).getName());
        assertEquals(0, files.get(0).getSize());

        EasyMock.verify(configuration, servletContext, httpRequest);
    }

    @Test
    public void testDeleteWithFullURI() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request);
        StoreResult result = service.store(temp, "delete-test.xml", "image/gif", "");

        assertEquals(0, result.getError());
        assertEquals("/file-mgr.file-servlet/delete-test.xml", result.getFileURI());

        File check = new File(testDir + "/some-dir", "delete-test.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        assertTrue(service.delete(result.getFileURI()));

        EasyMock.verify(httpRequest, servletContext, configuration);
    }

    @Test
    public void testDeleteWithRelativeURI() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/file-mgr.file-servlet");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.clearServletRequest();
        ServletObjectsHolder.setServletRequest(new HttpServletRequestWrapper(httpRequest));

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFileManagerService service = new DefaultFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request);
        StoreResult result = service.store(temp, "delete-test.xml", "image/gif", "");

        assertEquals(0, result.getError());
        assertEquals("/file-mgr.file-servlet/delete-test.xml", result.getFileURI());

        File check = new File(testDir + "/some-dir", "delete-test.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        assertTrue(service.delete("some-dir/delete-test.xml"));

        EasyMock.verify(httpRequest, servletContext, configuration);
    }
}