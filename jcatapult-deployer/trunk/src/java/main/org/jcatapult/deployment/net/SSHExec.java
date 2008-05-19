package org.jcatapult.deployment.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcatapult.deployment.DeploymentException;
import org.jcatapult.deployment.domain.BaseUserInfo;
import org.jcatapult.deployment.domain.SSHOptions;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * JSCH adapter for executing commands via SSH
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class SSHExec {
    private SSHOptions options;

    /**
     * Constructs a new SSH wrapper that will connect using the given SSH options.
     *
     * @param   options The SSH options.
     */
    public SSHExec(SSHOptions options) {
        this.options = options;
    }

    /**
     * Executes the given command via SSH.
     *
     * @param   command The command to execute.
     * @return The result from the remote server.
     */
    public String execute(String command) {
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

            final ChannelExec exec = (ChannelExec) session.openChannel("exec");
            exec.setCommand(command);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            exec.setOutputStream(output);
            exec.setExtOutputStream(output);

            // Setup the error stream
            ByteArrayOutputStream error = new ByteArrayOutputStream();
            exec.setErrStream(error);

            exec.connect();

            // wait for it to finish
            final AtomicBoolean finished = new AtomicBoolean(false);
            Thread thread =
                new Thread() {
                    public void run() {
                        while (!exec.isEOF()) {
                            try {
                                if (finished.get()) {
                                    return;
                                }

                                sleep(500);
                            } catch (Exception e) {
                                // ignored
                            }
                        }
                    }
                };

            thread.start();
            thread.join(0);
            finished.set(true);

            if (thread.isAlive()) {
                // ran out of time
                throw new DeploymentException("SSH command [" + command + "] timed out.");
            }

            int code = exec.getExitStatus();
            if (code != 0) {
                throw new DeploymentException("Unable to execute SSH command [" + command + "] due to [ " +
                    error.toString() + "]");
            }

            exec.disconnect();
            session.disconnect();

            return output.toString();
        } catch (JSchException e) {
            throw new DeploymentException(e);
        } catch (InterruptedException e) {
            throw new DeploymentException(e);
        }
    }
}
