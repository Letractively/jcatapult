package org.jcatapult.deployment;

import org.jcatapult.deployment.domain.DeploymentInfo;

/**
 * Interface for perfoming deployments
 *
 * This interface must be implemented by a deploy.groovy script
 * located in the following directory context:
 *
 * .jcatapult/deployment/&lt;domain>
 *
 * where &lt;domain> is equal to the domain you are deploying to.
 * This domain is the same domain configured in the deploy xml configuration file
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public interface Deployer {

    /**
     * Called to deploy
     *
     * @param deploymentInfo {@link DeploymentInfo} provided to groovy scripts.  This bean contains all the necessary information
     * for deploying artifacts to remote servers
     */
    public void deploy(DeploymentInfo deploymentInfo);
}
