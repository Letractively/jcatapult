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
    private String deployVersion;

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

    public String getDeployVersion() {
        return deployVersion;
    }

    public void setDeployVersion(String deployVersion) {
        this.deployVersion = deployVersion;
    }
}
