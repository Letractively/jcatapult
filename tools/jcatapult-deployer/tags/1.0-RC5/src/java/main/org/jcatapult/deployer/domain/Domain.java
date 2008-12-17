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

import java.util.ArrayList;
import java.util.List;

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * <p>Bean models a domain descriptor in the deploy xml configuration file</p>
 *
 * @author jhumphrey
 */
public class Domain implements Validatable {
    private String name;

    private List<Environment> envs = new ArrayList<Environment>();

    /**
     * <p>Gets the name of the environment</p>
     *
     * @return the name of the environment
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Sets the name of the environment</p>
     *
     * @param name the environment name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Gets a list of {@link org.jcatapult.deployer.domain.Environment} objects</p>
     *
     * @return list of {@link org.jcatapult.deployer.domain.Environment} objects
     */
    public List<Environment> getEnvs() {
        return envs;
    }

    /**
     * <p>Sets a list of {@link org.jcatapult.deployer.domain.Environment} objects</p>
     *
     * @param envs list of {@link org.jcatapult.deployer.domain.Environment} objects
     */
    public void setEnvs(List<Environment> envs) {
        this.envs = envs;
    }

    /**
     * <p>Adds an {@link Environment} object</p>
     *
     * @param env {@link org.jcatapult.deployer.domain.Environment} object
     */
    public void addEnv(Environment env) {
        this.envs.add(env);
    }

    /**
     * <p>Validates the bean</p>
     *
     * <p>required fields:</p>
     * <ul>
     *   <li>name/li>
     *   <li>at least 1 enviroment in the environment list/li>
     * </ul>
     *
     * @return the list of errors if invalid
     */
    public ErrorList validate() {
        ErrorList errorList = new ErrorList();

        if (StringTools.isEmpty(name)) {
            errorList.addError("The deploy xml configuration file 'domain' descriptor must define an attribute 'name' that uniquely identifies the domain you are deploying to.\nex: <domain name=\"jcatapult\">");
        }

        if (envs.size() == 0) {
            errorList.addError("The deploy xml configuration file 'domain' descriptor content must contain at least one 'environment' descriptor.\nex: <environment name=\"staging\">");
        } else {
            for (Environment env : envs) {
                errorList.addAllErrors(env.validate());
            }
        }

        return errorList;
    }
}
