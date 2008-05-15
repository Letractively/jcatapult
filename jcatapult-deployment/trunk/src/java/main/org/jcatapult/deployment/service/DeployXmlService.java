package org.jcatapult.deployment.service;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.jcatapult.deployment.domain.Deploy;
import org.jcatapult.deployment.domain.DeploymentProperties;
import org.jcatapult.deployment.domain.Environment;

/**
 * Uses apache commons configuration to extract properties from the deploy.xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeployXmlService extends CommonsConfigurationXmlService<DeploymentProperties> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    public DeploymentProperties unmarshall(File file) throws XmlServiceException {
        init(file);

        DeploymentProperties props = new DeploymentProperties();

        List<String> domainList = getConfig().getList("deploy.[@domain]");

        for (int i = 0; i < domainList.size(); i++) {
            Deploy deploy = new Deploy();
            deploy.setDomain(domainList.get(i));
            String deployKey = "deploy(" + i + ")";
            List<String> envList = getConfig().getList(deployKey + ".environment.[@type]");

            for (int j = 0; j < envList.size(); j++) {
                Environment env = new Environment();
                env.setType(envList.get(j));
                String envKey = deployKey + ".environment(" + j + ")";
                env.setHost(getConfig().getString(envKey + ".host"));
                env.setHostUsername(getConfig().getString(envKey + ".host-username"));
                env.setHostPassword(getConfig().getString(envKey + ".host-password"));
                env.setDbName(getConfig().getString(envKey + ".db-name"));
                env.setDbUsername(getConfig().getString(envKey + ".db-username"));
                env.setDbPassword(getConfig().getString(envKey + ".db-password"));
                deploy.addEnv(env);
            }
            props.addDeploy(deploy);
        }

        return props;
    }
}
