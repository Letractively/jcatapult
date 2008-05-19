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

package org.jcatapult.deployer.domain;

import java.io.File;

/**
 * <p>Bean that contains the information needed by the deploy.groovy script
 * to deploy the jar resource to the selected domain environemnt</p>
 *
 * @author jhumphrey
 */
public class DeploymentInfo {
    private String deployDomain;
    private Environment env;
    private Project project;
    private String deployJar;
    private File jatapultCacheDir;
    private File deployArchiveDir;
    private File deploymentDomainDir;

    /**
     * <p>The domain being deployed to</p>
     *
     * @return the deploy domain
     */
    public String getDeployDomain() {
        return deployDomain;
    }

    /**
     * <p>Sets the deploy domain</p>
     *
     * @param deployDomain the deploy domain
     */
    public void setDeployDomain(String deployDomain) {
        this.deployDomain = deployDomain;
    }

    /**
     * <p>Returns the {@link org.jcatapult.deployer.domain.Environment} being deployed to</p>
     *
     * @return the {@link org.jcatapult.deployer.domain.Environment} being deployed to
     */
    public Environment getEnv() {
        return env;
    }

    /**
     * <p>Sets the {@link org.jcatapult.deployer.domain.Environment} being deployed to</p>
     *
     * @param env {@link org.jcatapult.deployer.domain.Environment} being deployed to
     */
    public void setEnv(Environment env) {
        this.env = env;
    }

    /**
     * <p>Gets the {@link org.jcatapult.deployer.domain.Project} domain object</p>
     *
     * @return the {@link org.jcatapult.deployer.domain.Project} domain object
     */
    public Project getProject() {
        return project;
    }

    /**
     * <p>Sets the {@link org.jcatapult.deployer.domain.Project} domain object</p>
     *
     * @param project the {@link org.jcatapult.deployer.domain.Project} domain object
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * <p>Gets the deploy jar resource</p>
     *
     * @return the deploy jar resource
     */
    public String getDeployJar() {
        return deployJar;
    }

    /**
     * <p>Sets the deploy jar resource</p>
     *
     * @param deployJar the deploy jar resource
     */
    public void setDeployJar(String deployJar) {
        this.deployJar = deployJar;
    }

    /**
     * <p>Returns the jcatapult cache directory.  The value is always ${user.home}/.jcatapult</p>
     *
     * @return the jcatapult cache directory
     */
    public File getJatapultCacheDir() {
        return jatapultCacheDir;
    }

    /**
     * <p>Sets the jcatapult cache directory</p>
     *
     * @param jcatapultCacheDir the jcatapult cache directory
     */
    public void setJcatapultCacheDir(File jcatapultCacheDir) {
        this.jatapultCacheDir = jcatapultCacheDir;
    }

    /**
     * <p>Gets the deploy archive directory.  This directory is always ${user.home}/.jcatapult/deploy-archive</p>
     *
     * @return the deploy archive directory.  ${user.home}/.jcatapult/deploy-archive
     */
    public File getDeployArchiveDir() {
        return deployArchiveDir;
    }

    /**
     * <p>Sets the jcatapult deploy archive directory</p>
     *
     * @param deployArchiveDir the deploy archive directory
     */
    public void setDeployArchiveDir(File deployArchiveDir) {
        this.deployArchiveDir = deployArchiveDir;
    }

    /**
     * <p>Gets the deploy domain directory.  This is always ${user.home}/.jcatapult/deployment/&lt;domain>
     * where &lt;domain> = the domain deploying to.</p>
     *
     * @return the deployment domain directory
     */
    public File getDeploymentDomainDir() {
        return deploymentDomainDir;
    }

    /**
     * <p>Sets the deployment domain directory</p>
     *
     * @param deploymentDomainDir the deployment domain directory
     */
    public void setDeploymentDomainDir(File deploymentDomainDir) {
        this.deploymentDomainDir = deploymentDomainDir;
    }
}
