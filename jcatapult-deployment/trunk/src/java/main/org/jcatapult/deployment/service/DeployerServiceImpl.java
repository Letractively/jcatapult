package org.jcatapult.deployment.service;

import org.jcatapult.deployment.domain.DeployInfo;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeployerServiceImpl implements DeployerService {
    /**
     * {@inheritDoc}
     */
    public void deploy(DeployInfo deployInfo) {
        printHeader(deployInfo);

    }

    private void printHeader(DeployInfo deployInfo) {
        System.out.println("Deploying resource [" + deployInfo.getReleaseVersion() + "] with the following information:");
        System.out.println("- Server Host:" + deployInfo.getServerHost());
        System.out.println("- Home Dir:" + deployInfo.getHomeDir());
    }
}
