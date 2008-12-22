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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.service.FileManagerService;
import org.jcatapult.mvc.action.result.annotation.Header;
import org.jcatapult.mvc.action.result.annotation.Stream;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;

import com.google.inject.Inject;

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
@Header(code = "error", status = 500)
@Stream(type = "text/xml", name = "response")
public class FileManager {
    private static final JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(Connector.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    // Service
    protected final FileManagerService fileManagerService;

    // The command type
    public FileManagerCommand command;

    // Common parameters for all commands
    public String currentFolder;

    // Create folder command parameter
    public String newFolderName;

    // Input params for file upload
    public FileInfo newFile;

    // Response properties
    public long length;
    public InputStream stream;

    @Inject
    public FileManager(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

    public String execute() {
        // If this is an upload, skip processing and handle control over to the upload action
        if (command == FileManagerCommand.FileUpload) {
            return doUpload();
        }

        // Otherwise, process the request according to the command
        Connector connector = null;
        if (command == FileManagerCommand.CreateFolder) {
            connector = fileManagerService.createFolder(currentFolder, newFolderName, null);
        } else if (command == FileManagerCommand.GetFolders) {
            connector = fileManagerService.getFolders(currentFolder, null);
        } else if (command == FileManagerCommand.GetFoldersAndFiles) {
            connector = fileManagerService.getFoldersAndFiles(currentFolder, null);
        }

        marshal(connector);

        return "success";
    }

    /**
     * Performs the upload task using the {@link FileManagerService}.
     *
     * @return  The result to return from the execute method in order to provide a response.
     */
    protected String doUpload() {
        Connector connector = fileManagerService.upload(newFile.file, newFile.name, newFile.contentType, null, currentFolder);
        marshal(connector);
        return "success";
    }

    /**
     * Marshals the Connector instance into an InputStream that is accessible via the property named
     * {@link #stream}.
     *
     * @param   connector The connector to marshal.
     */
    protected void marshal(Connector connector) {
        try {
            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(connector, sw);

            String xml = sw.toString();
            this.stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (JAXBException e) {
            // Bad error!
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            // Bad error!
            throw new RuntimeException(e);
        }
    }
}