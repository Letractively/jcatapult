package org.jcatapult.deployment.service;

import org.jcatapult.deployment.domain.DeploymentInfo;

import com.google.inject.ImplementedBy;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
@ImplementedBy(DeployerServiceImpl.class)
public interface DeployerService {
    /**
     * Called to deploy the resources to the remote server
     *
     * @param deploymentInfo {@link org.jcatapult.deployment.domain.DeploymentInfo}
     */
    public void deploy(DeploymentInfo deploymentInfo);
}
