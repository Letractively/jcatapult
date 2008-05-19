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

package org.jcatapult.deployer.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcatapult.deployer.DeploymentException;
import org.jcatapult.deployer.domain.BaseUserInfo;
import org.jcatapult.deployer.domain.SSHOptions;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * <p>
 * This class uses the JSCH library to connect into the SSH protocol
 * and allow remote execution of scripts. Since this might use a
 * password or a passphrase, it will attempt to leverage the JDK 1.6
 * Console object via reflection. However, if that class doesn't exist
 * than it will use stdin and stdout for asking and retrieving passwords.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class SCP {
    private SSHOptions options;

    /**
     * <p>Constructs a new SCP wrapper that will connect using the given options.</p>
     *
     * @param   options The SSH options.
     */
    public SCP(SSHOptions options) {
        this.options = options;
    }

    /**
     * <p>Called to scp from a local directory to a remote directory</p>
     *
     * @param   from The file to copy.
     * @param   to The location on the remote server to copy to.
     */
    public void execute(File from, String to) {

        try {
            JSch jsch = new JSch();

            // Add the identity if it exists
            if (options.identity != null && new File(options.identity).isFile()) {
                jsch.addIdentity(options.identity);
            }

            // Add the known hosts if it exists
            if (options.knownHosts != null && new File(options.knownHosts).isFile()) {
                jsch.setKnownHosts(options.knownHosts);
            }

            Session session = jsch.getSession(options.username, options.host, options.port);
            session.setUserInfo(new BaseUserInfo(options.password, options.passphrase, options.trust));
            session.connect();

            ChannelExec exec = (ChannelExec) session.openChannel("exec");
            exec.setCommand("scp -p -t " + to);

            exec.setOutputStream(System.out);
            InputStream is = exec.getInputStream();
            OutputStream os = exec.getOutputStream();
            exec.connect();

            checkAck(is);

            // send "C0644 filesize filename", where filename should not include '/'
            String command = "C0644 " + from.length() + " " + from.getName() + "\n";
            os.write(command.getBytes());
            os.flush();
            checkAck(is);

            // Send contents of lfile
            FileInputStream fis = new FileInputStream(from);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }

                os.write(buf, 0, len); //out.flush();
            }

            fis.close();

            // send '\0'
            os.write(0);
            os.flush();
            checkAck(is);
            os.close();

            exec.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            throw new DeploymentException(e);
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }

    /**
     * Checks the acknowledgement response from the remote server
     *
     * @param is the input stream
     * @throws IOException thrown if there's an error reading from the input stream
     */
    private void checkAck(InputStream is) throws IOException {
        int b = is.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return;
        }

        if (b == -1) {
            throw new IOException("Invalid SCP acknowledgement response code [-1]");
        }

        if (b == 1 || b == 2) {
            StringBuilder build = new StringBuilder();
            int c;
            do {
                c = is.read();
                build.append((char) c);
            } while (c != '\n');

            throw new IOException(build.toString());
        }
    }
}