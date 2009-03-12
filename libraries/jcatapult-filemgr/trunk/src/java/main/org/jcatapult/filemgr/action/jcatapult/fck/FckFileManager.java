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
import java.io.InputStream;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jcatapult.filemgr.action.jcatapult.FileManagerCommand;
import org.jcatapult.filemgr.domain.CreateDirectoryResult;
import org.jcatapult.filemgr.domain.DirectoryData;
import org.jcatapult.filemgr.domain.FileData;
import org.jcatapult.filemgr.domain.Listing;
import org.jcatapult.filemgr.domain.StoreResult;
import org.jcatapult.filemgr.domain.fck.Connector;
import org.jcatapult.filemgr.domain.fck.CurrentFolder;
import org.jcatapult.filemgr.domain.fck.ErrorData;
import org.jcatapult.filemgr.domain.fck.Folder;
import org.jcatapult.filemgr.domain.fck.UploadResult;
import org.jcatapult.filemgr.service.FileManagerService;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Header;
import org.jcatapult.mvc.action.result.annotation.Stream;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;

import com.google.inject.Inject;

/**
 * <h3>FCK integration</h3>
 * <p>
 * The XML API provided by this file manager action is fully compliant with
 * the FCK editor's XML API. This API takes a GET or POST request and returns
 * an XML document in the HTTP response body. This XML document is specified
 * by a JCatapult XML schema, which can be found as part of the JCatapult
 * documentation.
 * </p>
 *
 * <h3>Request</h3>
 * <p>
 * The request coming into the this API contains a GET or POST parameter
 * named <code>command</code> that controls the operation for the service
 * to perform. This command can have one of these possible values:
 * </p>
 *
 * <table>
 * <tr><th>Value</th><th>Description</th></tr>
 * <tr><td>GetFolders</td><td>Returns a list of the folders on the server.
 *  The directory whose folders to returns is controlled by the parameter
 *  named <code>currentFolder</code>.</td></tr>
 * <tr><td>GetFoldersAndFiles</td><td>Returns a list of the folders and files
 *  on the server. The directory whose folders to returns is controlled by
 *  the parameter named <code>currentFolder</code>.</td></tr>
 * <tr><td>CreateFolder</td><td>Creates a new folder. This new folder is created
 *  in the folder specified using the parameter named <code>currentFolder</code>.
 *  The name of the new folder is controlled by the parameter named
 *  <code>newFolderName</code></td></tr>
 * <tr><td>FileUpload</td><td>This indicates that the HTTP request body
 *  contains a file upload. The multipart content is handled by Struts
 *  and the name to save the file on the server is controlled using the
 *  parameter named <code>NewFile</code>.</td></tr>
 * </table>
 *
 * <p>
 * These commands are also specified in the enumeration {@link FileManagerCommand}.
 * </p>
 *
 * <h3>Response</h3>
 * <p>
 * For the GetFolders, GetFoldersAndFiles and CreateFolder requests, the
 * response is an XML document that contains the information requested or
 * the result of the operation. However, for the FileUpload command, this
 * response is either an XML response or a JavaScript response, depending
 * on the FileManager class being used. This class returns an XML response
 * and the FCKFileManager sub-class returns a JavaScript response.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
@Header(code = "error", status = 500)
@Stream(type = "application/xml", name = "")
@Forward(code = "upload", page = "/file-mgr/fck-file-manager.ftl")
public class FckFileManager {
    private static final JAXBContext context;
    private final FileManagerService fileManagerService;

    static {
        try {
            context = JAXBContext.newInstance(Connector.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    // Output for file upload from the service.
    public Connector connector;

    // Response from the other requests
    public long length;
    public InputStream stream;

    // The command type
    public FileManagerCommand Command;

    // The file type
    public String Type;

    // Common parameters for all commands
    public String CurrentFolder;

    // Create folder command parameter
    public String NewFolderName;

    // Input params for file upload
    public FileInfo NewFile;

    public String uuid; // not used

    @Inject
    public FckFileManager(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

    public String execute() {
        // If this is an upload, skip processing and handle control over to the upload action
        if (Command == FileManagerCommand.FileUpload) {
            return doStore();
        }

        // Otherwise, process the request according to the command
        String directory = (Type != null) ? Type + "/" + CurrentFolder : CurrentFolder;
        Connector connector;
        if (Command == FileManagerCommand.CreateFolder) {
            CreateDirectoryResult result = fileManagerService.createDirectory(NewFolderName, directory);
            connector = translate(result);
        } else if (Command == FileManagerCommand.GetFolders) {
            Listing listing = fileManagerService.getFolders(directory);
            connector = translate(listing);
        } else if (Command == FileManagerCommand.GetFoldersAndFiles) {
            Listing listing = fileManagerService.getFoldersAndFiles(directory);
            connector = translate(listing);
        } else {
            throw new RuntimeException("Invalid command [" + Command + "]");
        }

        marshal(connector, context);

        return "success";
    }

    /**
     * Performs the upload task using the {@link FileManagerService}.
     *
     * @return  The result to return from the execute method in order to provide a response.
     */
    protected String doStore() {
        String directory = (Type != null) ? Type + "/" + CurrentFolder : CurrentFolder;
        StoreResult result = fileManagerService.store(NewFile.file, NewFile.name, NewFile.contentType, directory);
        connector = translate(result);
        return "upload";
    }

    /**
     * Marshals the Connector instance into an InputStream that is accessible via the property named
     * {@link #stream}.
     *
     * @param   connector The connector to marshal.
     * @param   context The JAXB context.
     */
    protected void marshal(Object connector, JAXBContext context) {
        try {
            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(connector, sw);

            String xml = sw.toString();
            byte[] ba = xml.getBytes("UTF-8");
            this.stream = new ByteArrayInputStream(ba);
            this.length = ba.length;
        } catch (JAXBException e) {
            // Bad error!
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            // Bad error!
            throw new RuntimeException(e);
        }
    }

    private Connector translate(CreateDirectoryResult result) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.toString());
        connector.setCurrentFolder(new CurrentFolder(result.getPath(), result.getURI()));
        connector.setResourceType(Type);
        if (result.getError() != 0) {
            connector.setError(new ErrorData(result.getError(), "Unable to create directory."));
        }

        return connector;
    }

    private Connector translate(Listing listing) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.toString());
        connector.setCurrentFolder(new CurrentFolder(listing.getPath(), listing.getURI()));
        connector.setResourceType(Type);

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

    private Connector translate(StoreResult result) {
        Connector connector = new Connector();
        connector.setCommand(FileManagerCommand.CreateFolder.toString());
        connector.setResourceType(Type);
        if (result.getError() != 0) {
            connector.setError(new ErrorData(1, "Unable to upload file."));
        } else {
            connector.setUploadResult(new UploadResult(result.getModifiedFileName(),
                result.getFileURI(), result.isChangedFileName(), result.getFile()));
        }

        return connector;
    }
}