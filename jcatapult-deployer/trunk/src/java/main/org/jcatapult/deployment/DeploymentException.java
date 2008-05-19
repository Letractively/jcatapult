package org.jcatapult.deployment;

/**
 * General exception thrown if there are problems during deployment
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class DeploymentException extends RuntimeException {
    public DeploymentException() {
        super();
    }

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentException(Throwable cause) {
        super(cause);
    }
}
