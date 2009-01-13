/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
 *
 */
package org.jcatapult.filemgr.action.jcatapult.fck;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jcatapult.filemgr.action.jcatapult.FileManager;
import org.jcatapult.filemgr.action.jcatapult.FileManagerCommand;
import org.jcatapult.filemgr.domain.CreateDirectoryResult;
import org.jcatapult.filemgr.domain.DirectoryData;
import org.jcatapult.filemgr.domain.FileData;
import org.jcatapult.filemgr.domain.Listing;
import org.jcatapult.filemgr.domain.StorageResult;
import org.jcatapult.filemgr.domain.fck.Connector;
import org.jcatapult.filemgr.domain.fck.CurrentFolder;
import org.jcatapult.filemgr.domain.fck.ErrorData;
import org.jcatapult.filemgr.domain.fck.Folder;
import org.jcatapult.filemgr.service.FileManagerService;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Header;
import org.jcatapult.mvc.action.result.annotation.Stream;

import com.google.inject.Inject;

/**
 * <p>
 * This class extends the {@link org.jcatapult.filemgr.action.jcatapult.FileManager} action and provides the
 * JavaScript response for file uploads.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Header(code = "error", status = 500)
@Stream(type = "text/xml", name = "result")
@Forward(code = "uplodate", page = "/file-mgr/fck-file-manager.ftl")
public class FckFileManager extends FileManager {
    private static final JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(Connector.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    // Output for file upload from the service.
    public Connector connector;
    public String type;

    @Inject
    public FckFileManager(FileManagerService fileManagerService) {
        super(fileManagerService);
    }

    @Override
    public String execute() {
        // If this is an upload, skip processing and handle control over to the upload action
        if (command == FileManagerCommand.FileUpload) {
            return doStore();
        }

        // Otherwise, process the request according to the command
        String directory = (type != null) ? type + "/" + currentFolder : currentFolder;
        Connector connector;
        if (command == FileManagerCommand.CreateFolder) {
            CreateDirectoryResult result = fileManagerService.createDirectory(newFolderName, directory);
            connector = translate(result);
        } else if (command == FileManagerCommand.GetFolders) {
            Listing listing = fileManagerService.getFolders(directory);
            connector = translate(listing);
        } else if (command == FileManagerCommand.GetFoldersAndFiles) {
            Listing listing = fileManagerService.getFoldersAndFiles(directory);
            connector = translate(listing);
        } else {
            throw new RuntimeException("Invalid command [" + command + "]");
        }

        marshal(connector, context);

        return "success";
    }

    /**
     * Performs the upload task using the {@link FileManagerService}.
     *
     * @return  The result to return from the execute method in order to provide a response.
     */
    @Override
    protected String doStore() {
        String directory = (type != null) ? type + "/" + currentFolder : currentFolder;
        StorageResult result = fileManagerService.store(newFile.file, newFile.name, newFile.contentType, directory);
        connector = translate(result);
        return "upload";
    }

    private Connector translate(CreateDirectoryResult result) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.toString());
        connector.setCurrentFolder(new CurrentFolder(result.getPath(), result.getURI()));
        connector.setResourceType(type);
        if (result.getError() != 0) {
            connector.setError(new ErrorData(result.getError(), "Unable to create directory."));
        }

        return connector;
    }

    private Connector translate(Listing listing) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.toString());
        connector.setCurrentFolder(new CurrentFolder(listing.getPath(), listing.getURI()));
        connector.setResourceType(type);

        List<FileData> files = listing.getFiles();
        for (FileData file : files) {
            connector.getFiles().add(new org.jcatapult.filemgr.domain.fck.FileData(file.getName(), file.getSize()));
        }

        List<DirectoryData> dirs = listing.getDirectories();
        for (DirectoryData dir : dirs) {
            connector.getFolders().add(new Folder(dir.getName()));
        }

        return connector;
    }

    private Connector translate(StorageResult result) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.toString());
        connector.setResourceType(type);
        if (result.getError() != 0) {
            connector.setError(new ErrorData(1, "Unable to upload file."));
        } else {
            connector.setUploadResult(new org.jcatapult.filemgr.domain.fck.UploadResult(result.getModifiedFileName(),
                result.getFileURI(), result.isChangedFileName(), result.getFile()));
        }

        return connector;
    }
}