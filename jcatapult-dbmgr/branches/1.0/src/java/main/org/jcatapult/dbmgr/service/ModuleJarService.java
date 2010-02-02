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
 */
package org.jcatapult.dbmgr.service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;

import org.jcatapult.dbmgr.domain.ModuleJar;

import com.google.inject.ImplementedBy;

/**
 * Service for interacting with {@link org.jcatapult.dbmgr.domain.ModuleJar} object
 *
 * User: jhumphrey
 * Date: Dec 14, 2007
 */
@ImplementedBy(DefaultModuleJarService.class)
public interface ModuleJarService {

    /**
     * Resolves jars.  The resolution strategy is implementation dependent but will generally resolve
     * via the classpath, file system or some other data store resource.
     *
     * @param projectXml the project.xml file belonging to the project to resolve jars for
     * @return a linked list of module jars
     */
    public LinkedList<ModuleJar> resolveJars(File projectXml);

    /**
     * Returns a list of JarEntry objects within the directory path specified
     *
     * @param moduleJar the module jar
     * @param dirPath the directory path.  the trailing forward slash isn't necessary.
     * @return list of JarEntry objects contained within the directory
     */
    public List<JarEntry> getJarDirectorySQLEntries(ModuleJar moduleJar, String dirPath);
}
