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

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * <p>Bean that models the environment descriptor defined in the deploy xml configuration file</p>
 *
 * @author jhumphrey
 */
public class Environment implements Validatable {
    private String name;
    private String host;
    private String hostUsername;
    private String hostPassword;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    /**
     * <p>Validates the bean.  Required fields:</p>
     *
     * <ul>
     *   <li>name</li>
     *   <li>host</li>
     *   <li>host-username</li>
     *   <li>host-password</li>
     * </ul>
     *
     * @return a list of errors if invalid.  empty list otherwise
     */
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

    /**
     * <p>returns the environment name</p>
     *
     * @return the environment name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>sets the environment name</p>
     *
     * @param name the environment name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Returns the environment host</p>
     *
     * @return the environment host
     */
    public String getHost() {
        return host;
    }

    /**
     * <p>Sets the environment host</p>
     *
     * @param host the environment host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * <p>Returns the host username</p>
     *
     * @return the host username
     */
    public String getHostUsername() {
        return hostUsername;
    }

    /**
     * <p>sets the host username</p>
     *
     * @param hostUsername the host username
     */
    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    /**
     * <p>Gets the host password</p>
     *
     * @return gets the host password
     */
    public String getHostPassword() {
        return hostPassword;
    }

    /**
     * <p>Sets the host password</p>
     *
     * @param hostPassword the host password
     */
    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    /**
     * <p>Gets the db name</p>
     *
     * @return the db name
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * <p>Sets the db name</p>
     *
     * @param dbName the db name
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * <p>Gets the db username</p>
     *
     * @return returns the db username
     */
    public String getDbUsername() {
        return dbUsername;
    }

    /**
     * <p>Sets the db username</p>
     *
     * @param dbUsername the db username
     */
    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    /**
     * <p>Gets the db password</p>
     *
     * @return the db password
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * <p>sets the db password</p>
     *
     * @param dbPassword the db password
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
