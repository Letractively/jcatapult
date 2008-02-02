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

import org.jcatapult.filemgr.domain.Connector;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the operations that are allowed for file support.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(FileManagerServiceImpl.class)
public interface FileManagerService {
    /**
     * <p>
     * This action handles all uploads via the file manager. This
     * handles verification of the MIME type of the upload as well as
     * saving the file to the correct location and returning a result
     * that the client can handle.
     * </p>
     *
     * <p>
     * The configuration system can be used to setup the allowed MIME types
     * using the <strong>jcatapult.file-mgr.file-upload.allowed-content-types</strong>
     * key. This property is an array property and can be specified using
     * multiple elements of the same name or via a comma-separated list
     * of values inside a single element like this:
     * </p>
     *
     * <pre>
     * &lt;jcatapult>
     *   &lt;file-mgr>
     *     &lt;file-upload>
     *       &lt;allowed-content-type>foo&lt;/allowed-content-type>
     *       &lt;allowed-content-type>bar&lt;/allowed-content-type>
     *     &lt;/file-upload>
     *   &lt;/file-mgr>
     * &lt;/jcatapult>
     *
     * or
     *
     * &lt;jcatapult>
     *   &lt;file-mgr>
     *     &lt;file-upload>
     *       &lt;allowed-content-type>foo,bar&lt;/allowed-content-type>
     *     &lt;/file-upload>
     *   &lt;/file-mgr>
     * &lt;/jcatapult>
     * </pre>
     *
     * <p>
     * The default list is:
     * </p>
     *
     * <pre>
     * image/jpeg
     * image/png
     * image/gif
     * application/x-shockwave-flash
     * application/pdf
     * </pre>
     *
     * <p>
     * In addition, the location that the files are saved to can be controlled
     * using the configuration parameter named <strong>jcatapult.file-mgr.file-servlet.dir</strong>.
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
     * you must also configure the FileServlet in web.xml so that it can serve
     * up the files appropriately. Or you could also use symlinks or an Apache
     * server is you prefer. In any case, you must set the configuration property
     * named <strong>jcatapult.file-mgr.file-servlet.prefix</strong> to the URL prefix that
     * will provide access to the files. If you are using the
     * {@link org.jcatapult.filemgr.servlet.FileServlet}, this will be the prefix
     * mapped in the servlet-mapping of the web.xml file.
     * </p>
     *
     * @param   file The file that was uploaded.
     * @param   fileName The name of the file that was sent from the browser.
     * @param   contentType The content type that was determined from the browser or server.
     * @param   fileType The type of file that the user is uploading (if this is specified
     *          by the browser/client)
     * @return  The result, which could be an error or a success.
     */
    Connector upload(File file, String fileName, String contentType, String fileType);

    /**
     * <p>
     * This action handles the creation of a new folder on disk. This uses
     * the configuration property named <strong>jcatapult.file-mgr.create-folder-allowed</strong>
     * to determine if the clients are allowed to create folders on the
     * file system or not. This defaults to true, so be sure to set this to
     * false if you do not want to allow directory creation.
     * </p>
     *
     * <p>
     * The main configuration property of <strong>jcatapult.file-mgr.file-servlet.dir</strong> is used
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
     * you must also configure the {@link org.jcatapult.filemgr.servlet.FileServlet}
     * in web.xml so that it can serve up the files appropriately. Or you could also
     * use symlinks or an Apache server is you prefer. In any case, you must set
     * the configuration property named <strong>jcatapult.file-mgr.file-servlet.prefix</strong>
     * to the URL prefix that will provide access to the files. If you are using the
     * {@link org.jcatapult.filemgr.servlet.FileServlet}, this will be the prefix
     * mapped in the servlet-mapping of the web.xml
     * file.
     * </p>
     *
     * @param   currentFolder The current folder the file manager has been asked to create a new directory
     *          within.
     * @param   newFolderName The name of the new folder.
     * @param   fileType The file type that the directory is being created for.
     * @return  The result XML object.
     */
    Connector createFolder(String currentFolder, String newFolderName, String fileType);

    /**
     * <p>
     * This action handles the listing of all of the files and folders within
     * a specific directory on the file system. Listings currently are not
     * configurable at all.
     * </p>
     *
     * <p>
     * The main configuration property of <strong>jcatapult.file-mgr.file-servlet.dir</strong> is used
     * by this method to determine the location on disk that files are being
     * uploaded to and managed by the file manager. This will be how the listings
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
     * you must also configure the {@link org.jcatapult.filemgr.servlet.FileServlet}
     * in web.xml so that it can serve up the files appropriately. Or you could
     * also use symlinks or an Apache server is you prefer. In any case, you must
     * set the configuration property named <strong>jcatapult.file-mgr.file-servlet.prefix</strong>
     * to the URL prefix that will provide access to the files. If you are using
     * the {@link org.jcatapult.filemgr.servlet.FileServlet}, this will be the
     * prefix mapped in the servlet-mapping of the web.xml
     * file.
     * </p>
     *
     * @param   currentFolder The current folder the file manager needs a listing for.
     * @param   type The file type the listing is for.
     * @return  The result XML object.
     */
    Connector getFoldersAndFiles(String currentFolder, String type);

    /**
     * <p>
     * This action handles the listing of all of the folders and not the files within
     * a specific directory on the file system. Listings currently are not
     * configurable at all.
     * </p>
     *
     * <p>
     * The main configuration property of <strong>jcatapult.file-mgr.file-servlet.dir</strong> is used
     * by this method to determine the location on disk that files are being
     * uploaded to and managed by the file manager. This will be how the listings
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
     * you must also configure the {@link org.jcatapult.filemgr.servlet.FileServlet}
     * in web.xml so that it can serve up the files appropriately. Or you could
     * also use symlinks or an Apache server is you prefer. In any case, you must
     * set the configuration property named <strong>jcatapult.file-mgr.file-servlet.prefix</strong>
     * to the URL prefix that will provide access to the files. If you are using
     * the {@link org.jcatapult.filemgr.servlet.FileServlet}, this will be the
     * prefix mapped in the servlet-mapping of the web.xml file.
     * </p>
     *
     * @param   currentFolder The current folder the file manager needs a listing for.
     * @param   type The file type the listing is for.
     * @return  The result XML object.
     */
    Connector getFolders(String currentFolder, String type);
}