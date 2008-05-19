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

package org.jcatapult.deployer.service;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.jcatapult.deployer.domain.Deploy;
import org.jcatapult.deployer.domain.Domain;
import org.jcatapult.deployer.domain.Environment;

/**
 * <p>Uses apache commons XMLConfiguration object to extract properties from the deploy.xml</p>
 *
 * <p>The service expects xml to be in the following format:</p>
 *
 * <pre>
 * &lt;deployment-configuration>
 * &lt;domain name="domain1">
 *   &lt;environment name="internal-qa">
 *     &lt;host>staging.jcatapult.org&lt;/host>
 *     &lt;host-username>staging.host.username&lt;/host-username>
 *     &lt;host-password>staging.host.password&lt;/host-password>
 *   &lt;/environment>
 * &lt;/domain>
 * &lt;domain name="domain2">
 *   &lt;environment name="external-qa">
 *     &lt;host>qa.jcatapult.org&lt;/host>
 *     &lt;host-username>qa.host.username&lt;/host-username>
 *     &lt;host-password>qa.host.password&lt;/host-password>
 *   &lt;/environment>
 *    &lt;environment name="staging">
 *      &lt;host>staging.jcatapult.org&lt;/host>
 *     &lt;host-username>staging.host.username&lt;/host-username>
 *      &lt;host-password>staging.host.password&lt;/host-password>
 *    &lt;/environment>
 *  &lt;/domain>
 *  &lt;domain name="domain3">
 *    &lt;environment name="production">
 *      &lt;host>production.jcatapult.org&lt;/host>
 *      &lt;host-username>production.host.username&lt;/host-username>
 *      &lt;host-password>production.host.password&lt;/host-password>
 *    &lt;/environment>
 *  &lt;/domain>
 * &lt;/deployment-configuration>
 * </pre>
 *
 * <p>required fields:</p>
 *
 * <ul>
 *   <li>at least one domain descriptor</li>
 *   <li>the domain name attribute</li>
 *   <li>at least one environment descriptor</li>
 *   <li>the environment name attribute</li>
 *   <li>the environment host</li>
 *   <li>the environment host username</li>
 *   <li>the environment host password</li>
 * </ul>
 *
 * @author jhumphrey
 */
public class DeployXmlService {

    /**
     * <p>Unmarshalls xml via the apache commons XMLConfiguration object</p>
     *
     * @param file the xml file to unmarshall
     * @return {@link org.jcatapult.deployer.domain.Deploy} bean
     * @throws XmlServiceException thrown if there's a problem during unmarshalling
     */
    @SuppressWarnings(value = "unchecked")
    public Deploy unmarshall(File file) throws XmlServiceException {
        Configuration config;
        try {
            config = new XMLConfiguration(file);
        }
        catch (ConfigurationException cex) {
            throw new XmlServiceException(cex);
        }

        Deploy deploy = new Deploy();
        deploy.setConfig(config);

        List<String> domainList = config.getList("domain.[@name]");

        for (int i = 0; i < domainList.size(); i++) {
            Domain domain = new Domain();
            domain.setName(domainList.get(i));
            String deployKey = "domain(" + i + ")";
            List<String> envList = config.getList(deployKey + ".environment.[@name]");

            for (int j = 0; j < envList.size(); j++) {
                Environment env = new Environment();
                env.setName(envList.get(j));
                String envKey = deployKey + ".environment(" + j + ")";
                env.setHost(config.getString(envKey + ".host"));
                env.setHostUsername(config.getString(envKey + ".host-username"));
                env.setHostPassword(config.getString(envKey + ".host-password"));
                env.setDbName(config.getString(envKey + ".db-name"));
                env.setDbUsername(config.getString(envKey + ".db-username"));
                env.setDbPassword(config.getString(envKey + ".db-password"));
                domain.addEnv(env);
            }
            deploy.addDomain(domain);
        }

        return deploy;
    }
}
