package org.jcatapult.deployment.domain;

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * Bean that models the environment descriptor in defined in the deploy xml configuration file
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class Environment implements Validatable {
    private String name;
    private String host;
    private String hostUsername;
    private String hostPassword;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    public ErrorList validate() {
        ErrorList errorList = new ErrorList();

        if (StringTools.isEmpty(name)) {
            errorList.addError("The deploy xml configuration file 'environment' descriptor must define an attribute 'name' with the value of the environment you " +
                "wish to deploy to.\nex: <environment name=\"staging\">");
        }

        if (StringTools.isEmpty(host)) {
            errorList.addError("The deploy xml configuration file 'environment' descriptor content must contain a 'host' descriptor with the content set to the host" +
                "you are deploying to.\nex: <host>staging.jatapult.org</host>");
        }

        if (StringTools.isEmpty(hostUsername)) {
            errorList.addError("The deploy xml configuration file 'environment' descriptor content must contain a 'host-username' descriptor with the content set to the username" +
                "of the host you are deploying to.\nex: <host-username>staging-username</host-username>");
        }

        if (StringTools.isEmpty(hostPassword)) {
            errorList.addError("The deploy xml configuration file 'environment' descriptor content must contain a 'host-password' descriptor with the content set to the password" +
                "of the host-username.\nex: <host-password>staging-password</host-password>");
        }

        return errorList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
