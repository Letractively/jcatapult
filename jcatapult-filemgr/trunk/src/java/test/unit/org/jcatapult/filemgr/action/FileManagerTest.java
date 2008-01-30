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
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.domain.Folder;
import org.jcatapult.filemgr.service.FileConfigurationImpl;
import org.jcatapult.filemgr.service.FileManagerServiceImpl;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.junit.Assert;
import org.junit.Test;

import net.java.io.FileTools;
import net.java.io.IOTools;

/**
 * <p>
 * This tests the file manager action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FileManagerTest {

    @Test
    public void testCreateDirNestedWithType() throws IOException {
        FileTools.prune("/tmp/jcatapult-filemgr");
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.prefix", "/files")).andReturn("/file-servlet");
        EasyMock.expect(configuration.getBoolean("file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FileManager fm = new FileManager(new FileManagerServiceImpl(new FileConfigurationImpl(configuration), servletContext));
        fm.setCommand(FileManagerCommand.CreateFolder);
        fm.setCurrentFolder("/deep/dir/");
        fm.setNewFolderName("test");
        fm.setType("Images");
        String result = fm.execute();
        Assert.assertEquals("success", result);

        System.out.println("XML is " + IOTools.read(fm.getResultStream(), "UTF-8"));

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
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        FileManagerServiceImpl service = new FileManagerServiceImpl(new FileConfigurationImpl(configuration), servletContext);
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
        EasyMock.expect(configuration.getString("file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn("files").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("files")).andReturn("/tmp/jcatapult-filemgr/some-dir");
        EasyMock.replay(servletContext);

        HttpServletRequest httpRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(httpRequest.getContextPath()).andReturn("/servlet-context/");
        EasyMock.replay(httpRequest);
        ServletObjectsHolder.setServletRequest(httpRequest);

        FileManagerServiceImpl service = new FileManagerServiceImpl(new FileConfigurationImpl(configuration), servletContext);
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