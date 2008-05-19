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

package org.jcatapult.deployer.domain;

/**
 * <p>data struct to represent ssh options for the {@link org.jcatapult.deployer.net.SSHExec}
 * and {@link org.jcatapult.deployer.net.SCP} JSCH adapters</p>
 *
 * @author jhumphrey
 */
public class SSHOptions {
    /**
     * <p>the host to ssh to</p>
     */
    public String host;

    /**
     * <p>the port.  defaults to 22</p>
     */
    public int port = 22;

    /**
     * <p>Known Hosts file</p>
     */
    public String knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";

    /**
     * <p>trust.  defaults to true;</p>
     */
    public boolean trust = true;

    /**
     * <p>the username of the account SSHing to</p>
     */
    public String username = System.getProperty("user.name");

    /**
     * <p>the password of the username</p>
     */
    public String password;

    /**
     * the identity
     */
    public String identity = System.getProperty("user.home") + "/.ssh/id_dsa";

    /**
     * <p>the passphrase.  defaults to empty string</p>
     */
    public String passphrase = "";

    /**
     * <p>the cipher</p>
     */
    public String cipher;
}
