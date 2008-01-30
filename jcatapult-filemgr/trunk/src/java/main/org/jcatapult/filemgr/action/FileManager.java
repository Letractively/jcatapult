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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.service.FileManagerService;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;

/**
 * <p>
 * This class provides an XML interface for file management. The XML interface
 * provides a mechanism for managing already existing files on the server.
 * </p>
 *
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
 * <h3>REST API</h3>
 * <p>
 * The API is standard RESTful interface, which means that it can be used by
 * other applications, not just FCK. Consult the XML schema documentation to
 * learn about the results from the API.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Results({
    @Result(name = "error", location = "", params = {"status", "500"}, type = "httpheader"),
    @Result(name = "success", location = "", params = {"contentType", "text/xml", "inputName", "resultStream"}, type = "stream")
})
public class FileManager extends BaseAction {
    // Service
    final FileManagerService fileManagerService;

    private FileManagerCommand command;
    private String currentFolder;
    private String newFolderName;
    private InputStream inputStream;

    // Input params for file upload
    private String type;
    private File file;
    private String contentType;
    private String fileName;

    @Inject
    public FileManager(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public File getNewFile() {
        return file;
    }

    public void setNewFile(File newFile) {
        this.file = newFile;
    }

    public String getNewFileContentType() {
        return contentType;
    }

    public void setNewFileContentType(String newFileContentType) {
        this.contentType = newFileContentType;
    }

    public String getNewFileFileName() {
        return fileName;
    }

    public void setNewFileFileName(String newFileFileName) {
        this.fileName = newFileFileName;
    }

    public FileManagerCommand getCommand() {
        return command;
    }

    public void setCommand(FileManagerCommand command) {
        this.command = command;
    }

    public String getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder = currentFolder;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }

    public InputStream getResultStream() {
        return inputStream;
    }

    @Override
    public String execute() {
        // If this is an upload, skip processing and handle control over to the upload action
        if (command == FileManagerCommand.FileUpload) {
            return doUpload();
        }

        // Otherwise, process the request according to the command
        Connector connector = null;
        if (command == FileManagerCommand.CreateFolder) {
            connector = fileManagerService.createFolder(currentFolder, newFolderName, getType());
        } else if (command == FileManagerCommand.GetFolders) {
            connector = fileManagerService.getFolders(currentFolder, getType());
        } else if (command == FileManagerCommand.GetFoldersAndFiles) {
            connector = fileManagerService.getFoldersAndFiles(currentFolder, getType());
        }

        marshal(connector);

        return Action.SUCCESS;
    }

    /**
     * Performs the upload task using the {@link FileManagerService}.
     *
     * @return  The result to return from the execute method in order to provide a response.
     */
    protected String doUpload() {
        Connector connector = fileManagerService.upload(file, fileName, contentType, type);
        marshal(connector);
        return SUCCESS;
    }

    /**
     * Marshals the Connector instance into an InputStream that is accessible via the property named
     * {@link #getResultStream()}.
     *
     * @param   connector The connector to marshal.
     */
    protected void marshal(Connector connector) {
        try {
            JAXBContext context = JAXBContext.newInstance(Connector.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(connector, sw);

            String xml = sw.toString();
            this.inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (JAXBException e) {
            // Bad error!
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            // Bad error!
            throw new RuntimeException(e);
        }
    }
}