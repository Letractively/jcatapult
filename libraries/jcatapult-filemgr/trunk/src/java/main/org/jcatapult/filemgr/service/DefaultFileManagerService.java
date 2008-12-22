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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.jcatapult.filemgr.action.jcatapult.FileManagerCommand;
import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.domain.CurrentFolder;
import org.jcatapult.filemgr.domain.Error;
import org.jcatapult.filemgr.domain.Folder;
import org.jcatapult.filemgr.domain.UploadResult;
import org.jcatapult.servlet.ServletObjectsHolder;

import com.google.inject.Inject;
import net.java.io.FileTools;
import net.java.lang.StringTools;

/**
 * <p>
 * This class implements the FileManagerService interface.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultFileManagerService implements FileManagerService {
    private static final Logger logger = Logger.getLogger(DefaultFileManagerService.class.getName());
    private final FileConfiguration configuration;
    private final ServletContext servletContext;
    private String[] allowedContentTypes;

    @Inject
    public DefaultFileManagerService(FileConfiguration configuration, ServletContext servletContext) {
        this.configuration = configuration;
        this.servletContext = servletContext;
        this.allowedContentTypes = configuration.getFileUploadAllowedContentTypes();
    }

    /**
     * {@inheritDoc}
     */
    public Connector upload(File file, String fileName, String contentType, String fileType,
            String directory) {
        Connector connector = new Connector();
        if (!file.exists() || file.isDirectory()) {
            logger.severe("The uploaded file [" + file.getAbsolutePath() + "] disappeared.");
            connector.setError(new Error(1, "File uplaod failed."));
            return connector;
        }

        // Set the directory
        if (StringTools.isEmpty(directory)) {
            directory = "";
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Inside upload action and handling fileName [" + fileName + "] with contentType [" +
                contentType + "]. The file was saved to [" + file.getAbsolutePath() + "]");
        }

        // Check the content type
        if (Arrays.binarySearch(allowedContentTypes, contentType) < 0) {
            connector.setError(new Error(1, "The file you are trying to upload is type [" + contentType +
                "] which is not an allowed type."));
            return connector;
        }

        String dirName = getFileDir(fileType) + directory;

        // Check for relative pathes
        String fullyQualifiedDirName = getFullyQualifiedDir(dirName);
        File dir = new File(fullyQualifiedDirName);
        if (!dir.exists()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Making directory [" + dir.getAbsolutePath() + "]");
            }

            if (!dir.mkdirs()) {
                logger.severe("Unable to create upload directory [" + dir.getAbsolutePath() + "]");
                connector.setError(new Error(1, "Unable to complete the file upload. Please try again later."));
                return connector;
            }
        }

        // Dissect the file into pieces.
        int dot = fileName.lastIndexOf(".");
        String fileNameWithoutExt = fileName;
        String ext = "";
        if (dot >= 0) {
            fileNameWithoutExt = fileName.substring(0, dot);
            ext = fileName.substring(dot + 1);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Determined file name [" + fileNameWithoutExt + "] and extenstion [" + ext + "]");
            }
        }

        // Figure out the new name in case that name is already taken. This adds the windows standard
        // (X) to the file name where X is a integer.
        String modifiedFileName = fileName;
        File newFileLocation = new File(dir, modifiedFileName);
        int counter = 1;
        while (newFileLocation.exists()) {
            modifiedFileName = fileNameWithoutExt + "(" + counter + ")" + "." + ext;
            newFileLocation = new File(dir, modifiedFileName);
            counter++;
        }

        try {
            FileTools.copy(file, newFileLocation);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error copying file [" + file.getAbsolutePath() + "] to [" +
                newFileLocation.getAbsolutePath() + "]", e);
            connector.setError(new Error(1, "Unable to complete the file upload. Please try again later."));
            return connector;
        }

        String fileURL = getExternalDirectoryURL(fileType) + "/" + newFileLocation.getName();

        logger.fine("Returning fileURL [" + fileURL + "]");

        connector.setUploadResult(new UploadResult(modifiedFileName, fileURL, !modifiedFileName.equals(fileName), newFileLocation));
        connector.setCommand(FileManagerCommand.FileUpload.name());
        return connector;
    }

    public Connector createFolder(String currentFolder, String newFolderName, String fileType) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.name());
        connector.setResourceType(fileType);

        CurrentFolder folder = new CurrentFolder();
        folder.setPath(currentFolder);
        folder.setUrl(getExternalDirectoryURL(fileType) + currentFolder);
        connector.setCurrentFolder(folder);

        if (!configuration.isCreateFolderAllowed()) {
            Error error = new Error();
            error.setNumber(103);
            connector.setError(error);
            return connector;
        }

        // Try to create the directory
        try {
            String dirName = getFileDir(fileType);
            String absolutePath = getFullyQualifiedDir(dirName);
            File dir = new File(absolutePath + "/" + currentFolder, newFolderName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (SecurityException se) {
            Error error = new Error();
            error.setNumber(103);
            connector.setError(error);
            return connector;
        }

        Error error = new Error();
        error.setNumber(0);
        connector.setError(error);
        return connector;
    }

    /**
     * {@inheritDoc}
     */
    public Connector getFoldersAndFiles(String currentFolder, String fileType) {
        return getListing(currentFolder, fileType, true);
    }

    /**
     * {@inheritDoc}
     */
    public Connector getFolders(String currentFolder, String fileType) {
        return getListing(currentFolder, fileType, false);
    }

    /**
     * Grabs the file upload directory configuration property and verifies that it is set. If it is not
     * setup, this throws an exception.
     *
     * @param   fileType (Optional) This file type can optionally be appended to the file directory location
     *          to build a more composite path for files of a specific type.
     * @return  The file directory.
     * @throws  RuntimeException If the configuration property is not setup.
     */
    private String getFileDir(String fileType) {
        String dirName = configuration.getFileServletDir();
        if (dirName == null) {
            throw new RuntimeException("The file-mgr.file-servlet.dir configuration property is not set and is required");
        }

        if (!StringTools.isTrimmedEmpty(fileType)) {
            dirName = dirName + "/" + fileType;
        }

        return dirName;
    }

    /**
     * Based on the file upload directory given, this determines the fully qualified location of that
     * directory on the file system. It also does some checks to ensure that the directory is correct
     * based on the type of web application deployment.
     *
     * @param   dirName The directory name to get the fully qualified version of.
     * @return  The fully qualified directory name.
     * @throws  RuntimeException If the directory given is relative to the web application content root
     *          but the ServletContext could not convert the path to a fully qualified location on disk.
     *          This usually means the web application is a WAR.
     */
    private String getFullyQualifiedDir(String dirName) {
        if (new File(dirName).isAbsolute()) {
            return dirName;
        }

        String fullyQualifiedDirName;
        fullyQualifiedDirName = servletContext.getRealPath(dirName);
        if (fullyQualifiedDirName == null) {
            throw new RuntimeException("The configuration property file-mgr.file-servlet.dir specified a relative " +
                "directory of [" + configuration.getFileServletDir() + "] however it appears that the web application " +
                "is running from a WAR and therefore you must use an absolute directory in order to save uploded " +
                "files elsewhere on the server.");
        }
        return fullyQualifiedDirName;
    }

    /**
     * Returns the externally usable file URL for the current file upload directory on the server and
     * the given file type, which might be null, empty or white space.
     *
     * @param   fileType (Optional) File type to append to the file URL.
     * @return  The external directory URL with no slash on the end.
     * @throws  RuntimeException If the file upload directory is absolute and there is not file-servlet
     *          prefix defined.
     */
    private String getExternalDirectoryURL(String fileType) {
        String fileDir = getFileDir(fileType);
        String fileURL;
        boolean relative = !fileDir.startsWith("/");
        if (relative) {
            fileURL = ServletObjectsHolder.getServletRequest().getContextPath() + fileDir;
        } else {
            String fileServletPrefix = configuration.getFileServletPrefix();
            if (fileServletPrefix == null) {
                throw new RuntimeException("The configuration property file-mgr.file-servlet.dir specified an absolute " +
                    "directory of [" + configuration.getFileServletDir() + "] however the configuration property " +
                    "file-mgr.file-servlet.prefix was not specified. This is required so that the browser can access files " +
                    "outside of the web application. Therefore, you must configure the FileServlet in the web.xml " +
                    "as well as set this configuration property to the same prefix that you mapped the servlet to in " +
                    "web.xml.");
            }

            fileURL = fileServletPrefix + (StringTools.isTrimmedEmpty(fileType) ? "" : "/" + fileType);
        }

        return fileURL;
    }

    /**
     * Gets the listings for a specific directory.
     *
     * @param   currentFolder The current folder to get the listing for.
     * @param   fileType The file type to get the listing for.
     * @param   includeFiles Whether or not the listing should include plain files or just directories.
     * @return  The listing.
     */
    private Connector getListing(String currentFolder, String fileType, boolean includeFiles) {
        Connector connector = new Connector();
        connector.setCommand(includeFiles ? FileManagerCommand.GetFoldersAndFiles.name() : FileManagerCommand.GetFolders.name());
        connector.setResourceType(fileType);

        CurrentFolder folder = new CurrentFolder();
        folder.setPath(currentFolder);
        folder.setUrl(getExternalDirectoryURL(fileType) + currentFolder);
        connector.setCurrentFolder(folder);

        // Try to locate the directory and get the listing
        String dirName = getFileDir(fileType);
        String absolutePath = getFullyQualifiedDir(dirName);
        File dir = new File(absolutePath, currentFolder);
        if (!dir.exists()) {
            return connector;
        }

        File[] listing = dir.listFiles();
        for (File file : listing) {
            if (includeFiles && file.isFile()) {
                org.jcatapult.filemgr.domain.File fileListing = new org.jcatapult.filemgr.domain.File();
                fileListing.setName(file.getName());
                fileListing.setSize(file.length() / 1024);
                connector.getFiles().add(fileListing);
            } else if (file.isDirectory()) {
                Folder folderListing = new Folder();
                folderListing.setName(file.getName());
                connector.getFolders().add(folderListing);
            }
        }

        return connector;
    }
}