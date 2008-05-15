package org.jcatapult.deployment.service;

import org.jcatapult.deployment.domain.DeploymentInfo;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeployerServiceImpl implements DeployerService {
    /**
     * {@inheritDoc}
     */
    public void deploy(DeploymentInfo deploymentInfo) {
        printHeader(deploymentInfo);

    }

    private void printHeader(DeploymentInfo deploymentInfo) {
        System.out.println("Deploying resource version [" + deploymentInfo.getDeployVersion() + "] with the following information:");
        System.out.println("- Server Host:" + deploymentInfo.getEnv().getHost());
    }
}
