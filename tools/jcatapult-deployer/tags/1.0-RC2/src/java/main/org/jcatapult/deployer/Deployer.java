/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.jcatapult.deployer;

import org.jcatapult.deployer.domain.DeploymentInfo;

/**
 * <p>Interface for perfoming deployments</p>
 *
 * <p>This interface must be implemented by a deploy.groovy script
 * located in the following directory context:</p>
 *
 * <pre>
 * .jcatapult/deployment/&lt;domain>
 * </pre>
 *
 * <p>where &lt;domain> is equal to the domain you are deploying to.
 * This domain is the same domain configured in the deploy xml configuration file</p>
 *
 * @author jhumphrey
 */
public interface Deployer {

    /**
     * <p>Called to deploy the jar resource to a particular domain environment</p>
     *
     * @param deploymentInfo {@link DeploymentInfo} provided to groovy scripts.  This bean contains all the necessary information
     * for deploying artifacts to remote servers
     */
    public void deploy(DeploymentInfo deploymentInfo);
}
