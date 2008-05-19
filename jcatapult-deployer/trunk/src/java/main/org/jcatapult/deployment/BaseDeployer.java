package org.jcatapult.deployment;

import java.io.File;

import org.jcatapult.deployment.domain.SSHOptions;
import org.jcatapult.deployment.net.SCP;
import org.jcatapult.deployment.net.SSHExec;

/**
 * Base class for groovy deployers. It is not necessary to extend this class but it does
 * provide convenience methods for inheriting secure copies and SSH exec capability
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public abstract class BaseDeployer implements Deployer {

    /**
     * Perfrms a secure copy
     *
     * @param host the host to scp to
     * @param username the username of the host account
     * @param password the password associated to the host account
     * @param file the file to scp
     * @param to the directory to scp to
     */
    public void doScp(String host, String username, String password, File file, String to) {

        System.out.print("SCPing [" + file.getAbsolutePath() + "] to host [" + host + "]...");

        SSHOptions options = new SSHOptions();
        options.host = host;
        options.username = username;
        options.password = password;
        new SCP(options).execute(file, to);

        System.out.println("done.");

    }

    /**
     * Executes a command via SSH
     *
     * @param host the host
     * @param username the host username
     * @param password the username password
     * @param command the command to execute
     * @return the result of the executed command
     */
    public String doSshExec(String host, String username, String password, String command) {

        System.out.println("Executing [" + command + "] on host [" + host + "]");

        SSHOptions options = new SSHOptions();
        options.host = host;
        options.username = username;
        options.password = password;

        return new SSHExec(options).execute(command);
    }
}
