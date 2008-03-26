package org.jcatapult.deployment.domain;

import java.io.File;

/**
 * Simple static struct to model server info extracted from the project deploy.xml file
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeployInfo {
    private String projectName;
    private String releaseVersion;
    private File projectXml;
    private String serverHost;
    private String serverUsername;
    private String serverPassword;
    private String envType;
    private String homeDir;
    private String workDir;
    private String fileDir;
    private String webDir;
    private String dbName;
    private String dbPassword;
    private String dbUsername;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public File getProjectXml() {
        return projectXml;
    }

    public void setProjectXml(File projectXml) {
        this.projectXml = projectXml;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerUsername() {
        return serverUsername;
    }

    public void setServerUsername(String serverUsername) {
        this.serverUsername = serverUsername;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getWebDir() {
        return webDir;
    }

    public void setWebDir(String webDir) {
        this.webDir = webDir;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }
}
