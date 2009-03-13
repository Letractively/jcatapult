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

import org.jcatapult.filemgr.domain.CreateDirectoryResult;
import org.jcatapult.filemgr.domain.Listing;
import org.jcatapult.filemgr.domain.StoreResult;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the operations that are allowed for file support.
 * </p>
 *
 * @author Brian Pontarelli and James Humphrey
 */
@ImplementedBy(DefaultFckFileManagerService.class)
public interface FckFileManagerService {
    /**
     * <p>
     * This method provides the service of storing files to disk.
     * </p>
     *
     * <p>
     * The location that the files are stored can be controlled
     * using the configuration parameter named <strong>jcatapult.file-mgr.storage-dir</strong>.
     * This directory can be a fully qualified path name anywhere on the machine
     * that the application is running on or it can be a directory relative to the
     * web application web root. In order to distinquish between these two, the
     * implementation assumes that all directories beginning with a / (forward-slash)
     * character are fully qualified and are directories on the local machine.
     * All paths that don't start with a / (forward-slash) are considered
     * relative to the web application context root.
     * </p>
     *
     * <p>
     * If you are using absolute pathes located elsewhere on the server, then
     * you must also configure the FileServlet in web.xml so that it can serve
     * up the files appropriately. Or you could also use symlinks or an Apache
     * server is you prefer. In any case, you must set the configuration property
     * named <strong>jcatapult.file-mgr.workflow-prefix</strong> to the URL prefix that
     * will provide access to the files. If you are using the
     * {@link org.jcatapult.filemgr.servlet.FileWorkflow}, this will be the prefix
     * mapped in the servlet-mapping of the web.xml file.
     * </p>
     *
     * @param   file The file that was uploaded.
     * @param   fileName The name of the file that was sent from the browser.
     * @param   contentType The content type that was determined from the browser or server.
     * @param   type The type from the FCK file manager.
     * @param   directory The name of the directory to place the uploaded file. This is appeneded
     *          to the directory specified <strong>jcatapult.file-mgr.storage-dir</strong>.
     * @return  The result, which could be an error or a success.
     */
    StoreResult store(File file, String fileName, String contentType, String type, String directory);

    /**
     * <p>
     * This action handles the creation of a new directory on disk. This uses
     * the configuration property named <strong>jcatapult.file-mgr.create-folder-allowed</strong>
     * to determine if the clients are allowed to create directories on the
     * file system or not. This defaults to true, so be sure to set this to
     * false if you do not want to allow directory creation.
     * </p>
     *
     * <p>
     * The main configuration property of <strong>jcatapult.file-mgr.storage-dir</strong> is used
     * by this method to determine the location on disk that files are being
     * uploaded to and managed by the file manager.
     * This directory can be a fully qualified path name anywhere on the machine
     * that the application is running or it can be a directory relative to the
     * web application web root. In order to distinquish between these two, this
     * class assumes that all directories beginning with a / (forward-slash)
     * character are fully qualified and are directories on the local machine.
     * All pathes that don't start with a / (forward-slash) are considered
     * relative to the web application context root.
     * </p>
     *
     * <p>
     * If you are using absolute pathes located elsewhere on the server, then
     * you must also configure the {@link org.jcatapult.filemgr.servlet.FileWorkflow}
     * in web.xml so that it can serve up the files appropriately. Or you could also
     * use symlinks or an Apache server is you prefer. In any case, you must set
     * the configuration property named <strong>jcatapult.file-mgr.workflow-prefix</strong>
     * to the URL prefix that will provide access to the files. If you are using the
     * {@link org.jcatapult.filemgr.servlet.FileWorkflow}, this will be the prefix
     * mapped in the servlet-mapping of the web.xml
     * file.
     * </p>
     *
     * @param   name The name of the new folder to create.
     * @param   type The type from the FCK file manager.
     * @param   directory The directory in which the new folder is to be created.
     * @return  The result.
     */
    CreateDirectoryResult createDirectory(String name, String type, String directory);

    /**
     * <p>
     * This action handles the listing of all of the files and folders within
     * a specific directory on the file system. Listings currently are not
     * configurable at all.
     * </p>
     *
     * <p>
     * The main configuration property of <strong>jcatapult.file-mgr.storage-dir</strong> is used
     * by this method to determine the location on disk that files are being
     * stored to and managed by the file manager. This will be how the listings
     * are generated, by calling {@link java.io.File#listFiles()} on those directory
     * instances.
     * </p>
     *
     * <p>
     * This directory can be a fully qualified path name anywhere on the machine
     * that the application is running or it can be a directory relative to the
     * web application web root. In order to distinquish between these two, this
     * class assumes that all directories beginning with a / (forward-slash)
     * character are fully qualified and are directories on the local machine.
     * All pathes that don't start with a / (forward-slash) are considered
     * relative to the web application context root.
     * </p>
     *
     * <p>
     * If you are using absolute pathes located elsewhere on the server, then
     * you must also configure the {@link org.jcatapult.filemgr.servlet.FileWorkflow}
     * in web.xml so that it can serve up the files appropriately. Or you could
     * also use symlinks or an Apache server is you prefer. In any case, you must
     * set the configuration property named <strong>jcatapult.file-mgr.workflow-prefix</strong>
     * to the URL prefix that will provide access to the files. If you are using
     * the {@link org.jcatapult.filemgr.servlet.FileWorkflow}, this will be the
     * prefix mapped in the servlet-mapping of the web.xml
     * file.
     * </p>
     *
     * @param   type The type from the FCK file manager.
     * @param   directory The directory to get the listing for.
     * @return  The result.
     */
    Listing getFoldersAndFiles(String type, String directory);

    /**
     * <p>
     * This action handles the listing of all of the folders and not the files within
     * a specific directory on the file system. Listings currently are not
     * configurable at all.
     * </p>
     *
     * <p>
     * The main configuration property of <strong>jcatapult.file-mgr.storage-dir</strong> is used
     * by this method to determine the location on disk that files are being
     * stored to and managed by the file manager. This will be how the listings
     * are generated, by calling {@link java.io.File#listFiles()} on those directory
     * instances.
     * </p>
     *
     * <p>
     * This directory can be a fully qualified path name anywhere on the machine
     * that the application is running or it can be a directory relative to the
     * web application web root. In order to distinquish between these two, this
     * class assumes that all directories beginning with a / (forward-slash)
     * character are fully qualified and are directories on the local machine.
     * All pathes that don't start with a / (forward-slash) are considered
     * relative to the web application context root.
     * </p>
     *
     * <p>
     * If you are using absolute pathes located elsewhere on the server, then
     * you must also configure the {@link org.jcatapult.filemgr.servlet.FileWorkflow}
     * in web.xml so that it can serve up the files appropriately. Or you could
     * also use symlinks or an Apache server is you prefer. In any case, you must
     * set the configuration property named <strong>jcatapult.file-mgr.workflow-prefix</strong>
     * to the URL prefix that will provide access to the files. If you are using
     * the {@link org.jcatapult.filemgr.servlet.FileWorkflow}, this will be the
     * prefix mapped in the servlet-mapping of the web.xml file.
     * </p>
     *
     * @param   type The type from the FCK file manager.
     * @param   directory The directory to get the listing for.
     * @return  The result.
     */
    Listing getFolders(String type, String directory);

    /**
     * <p>Deletes a file from disk that has been stored via the
     * {@link FckFileManagerService#store(java.io.File, String, String, String, String)} method.
     * The file URI supplied to this method should be exactly equal to
     * {@link org.jcatapult.filemgr.domain.StoreResult#getFileURI()} or the same with the
     * <strong>jcatapult.file-mgr.workflow-prefix</strong> removed.</p>
     *
     * <p>Example</p>
     *
     * <p>Lets say that I have the following configuration:</p>
     *
     * <p><strong>jcatapult.file-mgr.workflow-prefix</strong> = /files (this is the default)</p>
     *
     * <p>And, I store a file with the following:</p>
     * <code>StorageResult result = store(file, "test.gif", "image/gif", "images/test")</code>
     *
     * <p>After storing the file, I should get the following file URI when calling
     * {@link org.jcatapult.filemgr.domain.StoreResult#getFileURI()}:<br/><br/>
     *
     * file URI: <strong>/files/images/test/test.gif</strong></p>
     *
     * <p>When it's time to delete the file, you can specify the above file URI or a version of it with the
     * <strong>jcatapult.file-mgr.workflow-prefix</strong> removed.  Both are acceptable formats:</p>
     *
     * <ul>
     *   <li>delete("/files/images/test/test.gif")</li>
     *   <li>delete("images/test/test.gif")</li>
     * </ul>
     *
     * @param fileURI The file URI supplied to this method should be exactly equal to
     *        {@link org.jcatapult.filemgr.domain.StoreResult#getFileURI()} or the same with the
     *        <strong>jcatapult.file-mgr.workflow-prefix</strong> removed.
     * @return true if deleted, false otherwise
     */
    boolean delete(String fileURI);
}