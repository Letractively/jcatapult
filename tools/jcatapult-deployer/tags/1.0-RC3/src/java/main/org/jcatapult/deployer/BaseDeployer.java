/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
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

package org.jcatapult.deployer;

import java.io.File;

import org.jcatapult.deployer.domain.SSHOptions;
import org.jcatapult.deployer.net.SCP;
import org.jcatapult.deployer.net.SSHExec;

/**
 * <p>Base class for groovy deployers. It is not necessary to extend this class but it does
 * provide convenience methods for inheriting secure copies and SSH exec capability</p>
 *
 * @author jhumphrey
 */
public abstract class BaseDeployer implements Deployer {

    /**
     * <p>Perfrms a secure copy</p>
     *
     * @param host the host to scp to
     * @param username the username of the host account
     * @param password the password associated to the host account
     * @param file the file to scp
     * @param to the directory to scp to
     */
    public void scp(String host, String username, String password, File file, String to) {

        System.out.print("SCPing [" + file.getAbsolutePath() + "] to host [" + host + "]...");

        SSHOptions options = new SSHOptions();
        options.host = host;
        options.username = username;
        options.password = password;
        new SCP(options).execute(file, to);

        System.out.println("done.");

    }

    /**
     * <p>Executes a command via SSH</p>
     *
     * @param host the host
     * @param username the host username
     * @param password the username password
     * @param command the command to execute
     * @return the result of the executed command
     */
    public String sshExec(String host, String username, String password, String command) {

        System.out.println("Executing [" + command + "] on host [" + host + "]");

        SSHOptions options = new SSHOptions();
        options.host = host;
        options.username = username;
        options.password = password;

        return new SSHExec(options).execute(command);
    }
}