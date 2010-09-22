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
package org.jcatapult.filemgr.action.jcatapult.fck;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

import net.java.io.FileTools;
import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.filemgr.BaseTest;
import org.jcatapult.filemgr.action.jcatapult.FileManagerCommand;
import org.jcatapult.filemgr.service.DefaultFckFileManagerService;
import org.jcatapult.filemgr.service.DefaultFileConfiguration;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This tests the FCKFileManager action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class FckFileManagerTest extends BaseTest {
    @Test
    public void testRelativeSuccess() throws IOException {
        FileTools.prune(testDir);
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getString("jcatapult.file-mgr.storage-dir", System.getProperty("user.home") + "/data")).
            andReturn("some-dir").times(2);
        EasyMock.replay(configuration);

        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(servletContext.getRealPath("some-dir/File")).andReturn(testDir + "/some-dir/File");
        EasyMock.expect(servletContext.getContextPath()).andReturn("/foo");
        EasyMock.replay(servletContext);

        FckFileManager fm = new FckFileManager(new DefaultFckFileManagerService(new DefaultFileConfiguration(configuration), servletContext));
        fm.Command = FileManagerCommand.FileUpload;

        File temp = File.createTempFile("jcatapult-filemgr", "xml");
        temp.deleteOnExit();
        FileTools.copy(new File("src/java/test/unit/org/jcatapult/filemgr/action/jcatapult/test-file.xml"), temp);
        fm.NewFile = new FileInfo(temp, "foo-bar.xml", "image/gif");
        fm.Type = "File";
        fm.CurrentFolder = "/";

        String result = fm.execute();
        assertEquals("upload", result);

        System.out.println("" + fm.connector.getError());
        assertNull(fm.connector.getError());
        assertEquals(0, fm.connector.getUploadResult().getResultCode());
        assertEquals("/foo/some-dir/File/foo-bar.xml", fm.connector.getUploadResult().getFileURL());

        File check = new File(testDir + "/some-dir/File", "foo-bar.xml");
        assertTrue(check.exists());
        assertTrue(check.isFile());
        String contents = FileTools.read(check).toString();
        assertEquals("contents", contents);

        EasyMock.verify(servletContext, configuration);
    }
}
