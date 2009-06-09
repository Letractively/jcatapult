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

/**
 * <p>
 * This emnumeration stores the various types of actions that the FileManager
 * HTTP REST WebService interface will accept as well as the command values
 * for the response XML.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public enum FileManagerCommand {
    /**
     * GetFolders (XML): gets the list of the children folders of a folder.
     */
    GetFolders,

    /**
     * GetFoldersAndFiles (XML): similar to the above command, gets the list of the children folders and files of a folder.
     */
    GetFoldersAndFiles,

    /**
     * CreateFolder (XML): creates a child folder.
     */
    CreateFolder,

    /**
     * FileUpload (HTML): adds a file in a folder.
     */
    FileUpload
}