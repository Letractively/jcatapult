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
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.filemgr.domain.CreateDirectoryResult;
import org.jcatapult.filemgr.domain.FileData;
import org.jcatapult.filemgr.domain.DirectoryData;
import org.jcatapult.filemgr.domain.Listing;
import org.jcatapult.filemgr.domain.UploadResult;

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
    private final HttpServletRequest request;
    private String[] allowedContentTypes;

    @Inject
    public DefaultFileManagerService(FileConfiguration configuration, ServletContext servletContext, HttpServletRequest request) {
        this.configuration = configuration;
        this.servletContext = servletContext;
        this.request = request;
        this.allowedContentTypes = configuration.getFileUploadAllowedContentTypes();
    }

    /**
     * {@inheritDoc}
     */
    public UploadResult upload(File file, String fileName, String contentType, String directory) {
        UploadResult result = new UploadResult();
        if (!file.exists() || file.isDirectory()) {
            logger.severe("The uploaded file [" + file.getAbsolutePath() + "] disappeared.");
            result.setError(1); // file missing
            return result;
        }

        // Check the content type
        if (Arrays.binarySearch(allowedContentTypes, contentType) < 0) {
            result.setError(2); // invalid type
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
    public CreateDirectoryResult createDirectory(String name, String directory) {
        CreateDirectoryResult result = new CreateDirectoryResult();
        result.setPath(directory);
        result.setURI(addSlash(determineURI(directory)));

        if (!configuration.isCreateFolderAllowed()) {
            result.setError(1); // Can't create directories
            return result;
        }

        // Try to create the directory
        try {
            File dir = determineStoreDirectory(directory, name);
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
    public Listing getFoldersAndFiles(String directory) {
        return getListing(directory, true);
    }

    /**
     * {@inheritDoc}
     */
    public Listing getFolders(String currentFolder) {
        return getListing(currentFolder, false);
    }

    private File determineStoreDirectory(String... paths) {
        String dirName = configuration.getFileServletDir();
        if (dirName == null) {
            throw new RuntimeException("The file-mgr.file-servlet.dir configuration property is not set and is required");
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
                throw new RuntimeException("The configuration property file-mgr.file-servlet.dir specified a relative " +
                    "directory of [" + configuration.getFileServletDir() + "] however it appears that the web application " +
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
        String dirName = configuration.getFileServletDir();
        if (dirName == null) {
            throw new RuntimeException("The file-mgr.file-servlet.dir configuration property is not set and is required");
        }

        String uri;
        if (!new File(dirName).isAbsolute()) {
            uri = stripSlash(request.getContextPath()) + "/" + dirName;
        } else {
            uri = configuration.getFileServletPrefix();
            if (uri == null) {
                throw new RuntimeException("The configuration property file-mgr.file-servlet.dir specified an absolute " +
                    "directory of [" + configuration.getFileServletDir() + "] however the configuration property " +
                    "file-mgr.file-servlet.prefix was not specified. This is required so that the browser can access files " +
                    "outside of the web application. Therefore, you must configure the FileServlet in the web.xml " +
                    "as well as set this configuration property to the same prefix that you mapped the servlet to in " +
                    "web.xml.");
            }
        }

        uri = stripSlash(uri);

        for (String path : paths) {
            if (!StringTools.isTrimmedEmpty(path)) {
                if (path.startsWith("/")) {
                    uri = uri + path;
                } else {
                    uri = uri + "/" + path;
                }
            }

            uri = stripSlash(uri);
        }

        return uri;
    }

    /**
     * Gets the listings for a specific directory.
     *
     * @param   directory The directory to get the listing for.
     * @param   includeFiles Whether or not the listing should include plain files or just directories.
     * @return  The listing.
     */
    private Listing getListing(String directory, boolean includeFiles) {
        Listing listing = new Listing();
        listing.setPath(directory);
        listing.setURI(addSlash(determineURI(directory)));

        // Try to locate the directory and get the listing
        File dir = determineStoreDirectory(directory);
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

    private String stripSlash(String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        }

        return s;
    }

    private String addSlash(String s) {
        if (s.endsWith("/")) {
            return s;
        }

        return s + "/";
    }
}