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
public class DefaultFckFileManagerServiceTest extends BaseTest {
    @Test
    public void storeMissingDirName() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(null);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        try {
            service.store(new File("project.xml"), "foo.xml", "image/gif", "File", null);
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
                andReturn("storage-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("storage-dir/File")).andReturn(null);
        EasyMock.replay(servletContext);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        try {
            service.store(new File("project.xml"), "foo.xml", "image/gif", "File", null);
            fail("Should have failed");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("jcatapult.file-mgr.storage-dir"));
            assertTrue(e.getMessage().contains("storage-dir"));
        }

        EasyMock.verify(servletContext, configuration);
    }

    @Test
    public void storeRelativeSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("storage-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("storage-dir/File")).andReturn(testDir + "/storage-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo");
        EasyMock.replay(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "File", "/");
        assertEquals(0, result.getError());
        assertEquals("/foo/storage-dir/File/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir", "foo-bar.xml");
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
                andReturn("storage-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("storage-dir/Image/my-images")).andReturn(testDir + "/storage-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/foo");
        EasyMock.replay(httpRequest);

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "Image", "my-images");
        assertEquals(0, result.getError());
        assertEquals("/foo/storage-dir/Image/my-images/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir", "foo-bar.xml");
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
                andReturn(testDir + "/storage-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir"); // Exception message
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

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        try {
            service.store(temp, "foo-bar.xml", "image/gif", "File", "/");
            fail("Should have failed");
        } catch (Exception e) {
        }
    }

    @Test
    public void storeAbsoluteSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
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

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "File", "/");

        assertEquals(0, result.getError());
        assertEquals("/prefix/File/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir/File", "foo-bar.xml");
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
                andReturn(testDir + "/storage-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/prefix");
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

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "File", "my-images");

        assertEquals(0, result.getError());
        assertEquals("/prefix/File/my-images/foo-bar.xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir/File/my-images", "foo-bar.xml");
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
                andReturn(testDir + "/storage-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/prefix");
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

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        StoreResult result = service.store(temp, "foo-bar.xml", "image/gif", "File", "my-images");

        assertEquals(0, result.getError());
        assertTrue(result.isChangedFileName());
        assertEquals("/prefix/File/my-images/foo-bar(1).xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir/File/my-images", "foo-bar(1).xml");
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
                andReturn(testDir + "/storage-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        CreateDirectoryResult result = service.createDirectory("test", "File", "/");
        assertEquals("/prefix/File/", result.getURI());
        assertEquals("/", result.getPath());

        File dir = new File(testDir + "/storage-dir/File/test");
        assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void createDirNestedWithType() {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, request);
        CreateDirectoryResult result = service.createDirectory("test", "Flash", "/deep/dir/");
        assertEquals("/prefix/Flash/deep/dir/", result.getURI());
        assertEquals("/deep/dir/", result.getPath());

        File dir = new File(testDir + "/storage-dir/Flash/deep/dir/test");
        assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }

    @Test
    public void getFolders() {
        FileTools.prune(testDir);
        new File(testDir + "/storage-dir/test").mkdirs();
        new File(testDir + "/storage-dir/test2").mkdirs();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files/File")).andReturn(testDir + "/storage-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context");
        EasyMock.replay(httpRequest);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        Listing listing = service.getFolders("File", "/");
        assertEquals("/servlet-context/files/File/", listing.getURI());
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
        new File(testDir + "/storage-dir/test").mkdirs();
        new File(testDir + "/storage-dir/test2").mkdirs();
        new File(testDir + "/storage-dir/file").createNewFile();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files/File")).andReturn(testDir + "/storage-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context");
        EasyMock.replay(httpRequest);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        Listing listing = service.getFoldersAndFiles("File", "/");
        assertEquals("/servlet-context/files/File/", listing.getURI());
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
        new File(testDir + "/File/storage-dir").mkdirs();
        new File(testDir + "/File/storage-dir2").mkdirs();
        new File(testDir + "/File/file").createNewFile();
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).andReturn(testDir);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).andReturn("/files");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).andReturn(testDir);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(httpRequest);

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration),
                servletContext, httpRequest);
        Listing listing = service.getFoldersAndFiles("File", "/");
        assertEquals("/files/File/", listing.getURI());
        assertEquals("/", listing.getPath());

        List<DirectoryData> folders = listing.getDirectories();
        assertEquals(2, folders.size());
        assertTrue(folders.get(0).getName().equals("storage-dir") || folders.get(0).getName().equals("storage-dir2"));
        assertTrue(folders.get(1).getName().equals("storage-dir") || folders.get(1).getName().equals("storage-dir2"));

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
                andReturn(testDir + "/storage-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
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

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request);
        StoreResult result = service.store(temp, "delete-test.xml", "image/gif", "File", "/");

        assertEquals(0, result.getError());
        assertEquals("/prefix/File/delete-test.xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir/File", "delete-test.xml");
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
                andReturn(testDir + "/storage-dir").times(2);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
                andReturn(testDir + "/storage-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.workflow-prefix", "/files")).
                andReturn("/prefix");
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

        DefaultFckFileManagerService service = new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request);
        StoreResult result = service.store(temp, "delete-test.xml", "image/gif", "File", "/");

        assertEquals(0, result.getError());
        assertEquals("/prefix/File/delete-test.xml", result.getFileURI());

        File check = new File(testDir + "/storage-dir/File", "delete-test.xml");
        assertTrue(check.exists() && check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        assertTrue(service.delete("storage-dir/File/delete-test.xml"));

        EasyMock.verify(httpRequest, servletContext, configuration);
    }
}