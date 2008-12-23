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

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.filemgr.BaseTest;
import org.jcatapult.filemgr.service.DefaultFileConfiguration;
import org.jcatapult.filemgr.service.DefaultFileManagerService;
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
public class FileManagerTest extends BaseTest {

    @Test
    public void testCreateDirNestedWithType() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getStringArray("jcatapult.file-mgr.file-upload.allowed-content-types")).andReturn(null);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir");
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.prefix", "/files")).andReturn("/file-servlet");
        EasyMock.expect(configuration.getBoolean("jcatapult.file-mgr.create-folder-allowed", true)).andReturn(true);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.file-servlet.dir", System.getProperty("user.home") + "/data")).
            andReturn(testDir + "/some-dir");
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.replay(servletContext);

        FileManager fm = new FileManager(new DefaultFileManagerService(new DefaultFileConfiguration(configuration), servletContext, request));
        fm.command = FileManagerCommand.CreateFolder;
        fm.currentFolder = "/deep/dir/";
        fm.newFolderName = "test";
        String result = fm.execute();
        Assert.assertEquals("success", result);

        System.out.println("XML is " + IOTools.read(fm.stream, "UTF-8"));

        File dir = new File(testDir + "/some-dir/deep/dir/test");
        Assert.assertTrue(dir.isDirectory());

        EasyMock.verify(configuration, servletContext);
    }
}