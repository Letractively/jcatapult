package org.jcatapult.deployment.domain;

import java.io.File;

/**
 * Bean that contains the information needed to deploy to the remote server
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeploymentInfo {
    private String deployDomain;
    private Environment env;
    private Project project;
    private String deployJar;
    private File jatapultCacheDir;
    private File deployArchiveDir;
    private File deploymentDomainDir;

    public String getDeployDomain() {
        return deployDomain;
    }

    public void setDeployDomain(String deployDomain) {
        this.deployDomain = deployDomain;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDeployJar() {
        return deployJar;
    }

    public void setDeployJar(String deployJar) {
        this.deployJar = deployJar;
    }

    public File getJatapultCacheDir() {
        return jatapultCacheDir;
    }

    public void setJcatapultCacheDir(File jcatapultCacheDir) {
        this.jatapultCacheDir = jcatapultCacheDir;
    }

    public File getDeployArchiveDir() {
        return deployArchiveDir;
    }

    public void setDeployArchiveDir(File deployArchiveDir) {
        this.deployArchiveDir = deployArchiveDir;
    }

    public File getDeploymentDomainDir() {
        return deploymentDomainDir;
    }

    public void setDeploymentDomainDir(File deploymentDomainDir) {
        this.deploymentDomainDir = deploymentDomainDir;
    }
}
