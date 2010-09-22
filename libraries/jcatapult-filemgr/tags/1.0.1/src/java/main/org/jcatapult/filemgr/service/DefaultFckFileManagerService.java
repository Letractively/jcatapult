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

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import net.java.io.FileTools;
import net.java.lang.StringTools;
import org.jcatapult.filemgr.domain.CreateDirectoryResult;
import org.jcatapult.filemgr.domain.DirectoryData;
import org.jcatapult.filemgr.domain.FileData;
import org.jcatapult.filemgr.domain.Listing;
import org.jcatapult.filemgr.domain.StoreResult;

/**
 * <p>
 * This class implements the FileManagerService interface.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultFckFileManagerService implements FckFileManagerService {
    private static final Logger logger = Logger.getLogger(DefaultFckFileManagerService.class.getName());
    private final FileConfiguration configuration;
    private final ServletContext servletContext;

    @Inject
    public DefaultFckFileManagerService(FileConfiguration configuration, ServletContext servletContext) {
        this.configuration = configuration;
        this.servletContext = servletContext;
    }

    /**
     * {@inheritDoc}
     */
    public StoreResult store(File file, String fileName, String contentType, String type, String directory) {
        directory = stripSlashes(stripSlashes(type) + "/" + stripSlashes(directory));

        StoreResult result = new StoreResult();
        if (!file.exists() || file.isDirectory()) {
            logger.severe("The file to store [" + file.getAbsolutePath() + "] no longer exists. " +
                "Please verify that it was uploaded successfully.");
            result.setError(1); // file missing
            return result;
        }

        // Set the directory
        File dir = determineStoreDirectory(directory);
        if (dir == null) {
            result.setError(3); // can't create directory
            return result;
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
            result.setError(4); // copy error
            return result;
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Inside upload action and handling fileName [" + fileName + "] with contentType [" +
                contentType + "]. The file was saved to [" + newFileLocation.getAbsolutePath() + "]");
        }

        String fileURI = determineURI(directory, newFileLocation.getName());

        logger.fine("Returning fileURI [" + fileURI + "]");

        result.setModifiedFileName(modifiedFileName);
        result.setFileURI(fileURI);
        result.setChangedFileName(!modifiedFileName.equals(fileName));
        result.setFile(newFileLocation);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public CreateDirectoryResult createDirectory(String name, String type, String directory) {
        // Normalize everything
        String path = stripSlashes(type) + "/" + stripSlashes(directory);
        name = stripSlashes(name);

        CreateDirectoryResult result = new CreateDirectoryResult();
        result.setPath(wrapWithSlashes(directory));
        result.setURI(wrapWithSlashes(determineURI(path)));

        if (!configuration.isCreateFolderAllowed()) {
            result.setError(1); // Can't create directories
            return result;
        }

        // Try to create the directory
        try {
            File dir = determineStoreDirectory(path, name);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    result.setError(2); // Directory create failed
                }
            }
        } catch (SecurityException se) {
            result.setError(3); // Security error
            return result;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Listing getFoldersAndFiles(String type, String directory) {
        return getListing(type, directory, true);
    }

    /**
     * {@inheritDoc}
     */
    public Listing getFolders(String type, String directory) {
        return getListing(type, directory, false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete(String fileURI) {
        String location = configuration.getFileStorageDir();
        String prefix = configuration.getFileWorkflowPrefix();

        // strip the prefix (if it exists), this will give us the exact URI relative to the storage directory
        String relativeToStorageDirURI = fileURI.replaceFirst(prefix, "");

        File file = new File(location, relativeToStorageDirURI);

        return !file.exists() || file.delete();
    }

    private File determineStoreDirectory(String... paths) {
        String dirName = configuration.getFileStorageDir();
        if (dirName == null) {
            throw new RuntimeException("The jcatapult.file-mgr.storage-dir configuration property is not set and is required");
        }

        for (String path : paths) {
            if (!StringTools.isTrimmedEmpty(path)) {
                dirName = dirName + "/" + path;
            }
        }

        // Check for relative pathes
        File dir = new File(dirName);
        if (!dir.isAbsolute()) {
            String fullyQualifiedDirName = servletContext.getRealPath(dirName);
            if (fullyQualifiedDirName == null) {
                throw new RuntimeException("The configuration property jcatapult.file-mgr.storage-dir specified a relative " +
                    "directory of [" + configuration.getFileStorageDir() + "] however it appears that the web application " +
                    "is running from a WAR and therefore you must use an absolute directory in order to save uploded " +
                    "files elsewhere on the server.");
            }

            dir = new File(fullyQualifiedDirName);
        }

        if (!dir.exists()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Making directory [" + dir.getAbsolutePath() + "]");
            }

            if (!dir.mkdirs()) {
                logger.severe("Unable to create upload directory [" + dir.getAbsolutePath() + "]");
                return null;
            }
        }

        return dir;
    }

    private String determineURI(String... paths) {
        String dirName = configuration.getFileStorageDir();
        if (dirName == null) {
            throw new RuntimeException("The jcatapult.file-mgr.storage-dir configuration property is not set and is required");
        }

        String uri;
        if (new File(dirName).isAbsolute()) {
            uri = configuration.getFileWorkflowPrefix();
        } else {
            uri = servletContext.getContextPath() + "/" + dirName;
        }

        for (String path : paths) {
            uri = wrapWithSlashes(uri);

            if (!StringTools.isTrimmedEmpty(path)) {
                if (path.startsWith("/")) {
                    uri = uri + path.substring(1);
                } else {
                    uri = uri + path;
                }
            }
        }

        return uri;
    }

    /**
     * Gets the listings for a specific directory.
     *
     * @param   type The type from the FCK file manager.
     * @param   directory The directory to get the listing for.
     * @param   includeFiles Whether or not the listing should include plain files or just directories.
     * @return  The listing.
     */
    private Listing getListing(String type, String directory, boolean includeFiles) {
        String path = stripSlashes(stripSlashes(type) + "/" + stripSlashes(directory));

        Listing listing = new Listing();
        listing.setPath(wrapWithSlashes(directory));
        listing.setURI(wrapWithSlashes(determineURI(path)));

        // Try to locate the directory and get the listing
        File dir = determineStoreDirectory(path);
        if (dir == null) {
            return listing;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (includeFiles && file.isFile()) {
                FileData fileData = new FileData();
                fileData.setName(file.getName());
                fileData.setSize(file.length() / 1024);
                listing.addFile(fileData);
            } else if (file.isDirectory()) {
                DirectoryData directoryData = new DirectoryData();
                directoryData.setName(file.getName());
                listing.addDirectory(directoryData);
            }
        }

        return listing;
    }

    private String stripSlashes(String name) {
        if (name == null) {
            return "";
        }

        if (name.length() > 0 && name.startsWith("/")) {
            name = name.substring(1);
        }
        if (name.length() > 0 && name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    private String wrapWithSlashes(String path) {
        if (!path.equals("/")) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            } if (!path.endsWith("/")) {
                path = path + "/";
            }
        }

        return path;
    }
}
