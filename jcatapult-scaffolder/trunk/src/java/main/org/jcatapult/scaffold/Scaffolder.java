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
package org.jcatapult.scaffold;

import java.io.File;

/**
 * <p>
 * This interface must be implemented by individual scaffolders. It
 * is the mechanism that the scaffolder utility uses to invoke the
 * scaffolders.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Scaffolder {
    /**
     * Runs the scaffolder.
     */
    void execute();

    /**
     * Sets the directory on the file system that the scaffolder is defined in. This directory can be
     * used by the scaffolders to find additional files such as templates.
     *
     * @param   dir The scaffolder's home directory.
     */
    void setDir(File dir);

    /**
     * Sets whether or not the scaffolder should overwrite files during execution.
     *
     * @param   overwrite The overwrite flag.
     */
    void setOverwrite(boolean overwrite);
}