package org.jcatapult.deployment;

import org.jcatapult.deployment.domain.DeploymentInfo;

/**
 * Interface implemented by deploy.groovy scripts to perform deployment
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
