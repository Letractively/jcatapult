package org.jcatapult.deployment.domain;

/**
 * data struct to represent ssh options for the {@link org.jcatapult.deployment.net.SSHExec} and {@link org.jcatapult.deployment.net.SCP} JSCH adapters
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class SSHOptions {
    /**
     * the hose to ssh to
     */
    public String host;

    /**
     * the port.  defaults to 22
     */
    public int port = 22;

    /**
     * Known Hosts file
     */
    public String knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";

    /**
     * trust.  defaults to true;
     */
    public boolean trust = true;

    /**
     * the username of the account SSHing to
     */
    public String username = System.getProperty("user.name");

    /**
     * the password of the username
     */
    public String password;

    /**
     * the identity
     */
    public String identity = System.getProperty("user.home") + "/.ssh/id_dsa";

    /**
     * the passphrase.  defaults to empty string
     */
    public String passphrase = "";

    /**
     * the cipher
     */
    public String cipher;
}
