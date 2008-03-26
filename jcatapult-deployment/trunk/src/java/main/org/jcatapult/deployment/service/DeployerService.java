package org.jcatapult.deployment.service;

import org.jcatapult.deployment.domain.DeployInfo;

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
     * @param deployInfo {@link DeployInfo}
     */
    public void deploy(DeployInfo deployInfo);
}
