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

import org.apache.commons.configuration.Configuration;

import net.java.error.ErrorList;
import net.java.validate.Validatable;

/**
 * <p>Bean to model deployment properties defined in the deploy xml configuration file</p>
 *
 * @author jhumphrey
 */
public class Deploy implements Validatable {
    private List<Domain> domains = new ArrayList<Domain>();

    private Configuration config;

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    /**
     * <p>Adds one {@link Domain} to the deploys list</p>
     *
     * @param domain {@link Domain}
     */
    public void addDomain(Domain domain) {
        this.domains.add(domain);
    }

    /**
     * <p>Returns the apache commons configuration object.  This object can be used to access
     * any additional information from the project.xml file</p>
     *
     * @return apache commons configuration object.
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * <p>Sets the apache commons configuration object</p>
     *
     * @param config the apache commons configuration object
     */
    public void setConfig(Configuration config) {
        this.config = config;
    }

    /**
     * <p>Called to validate bean properties.  returns a list of errors or empty list</p>
     *
     * <p>required fields:</p>
     * <ul>
     *  <li>must be at least 1 domain in the domains list</li>
     * </ul>
     *
     * @return an error list.  empty if no errors exist
     */
    public ErrorList validate() {
        ErrorList errorList = new ErrorList();
        if (domains.size() == 0) {
            errorList.addError("The deploy xml configuration file must contain at least one 'domain' descriptor.\nex: <domain name=\"jcatapult\">");
        } else {
            for (Domain domain : domains) {
                errorList.addAllErrors(domain.validate());
            }
        }

        return errorList;
    }
}
