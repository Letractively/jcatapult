package org.jcatapult.deployment.service;

import java.io.File;
import java.util.List;

import org.jcatapult.deployment.domain.Domain;
import org.jcatapult.deployment.domain.Deploy;
import org.jcatapult.deployment.domain.Environment;

/**
 * Uses apache commons configuration to extract properties from the deploy.xml
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeployXmlService extends CommonsConfigurationXmlService<Deploy> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    public Deploy unmarshall(File file) throws XmlServiceException {
        init(file);

        Deploy props = new Deploy();

        List<String> domainList = getConfig().getList("domain.[@name]");

        for (int i = 0; i < domainList.size(); i++) {
            Domain domain = new Domain();
            domain.setName(domainList.get(i));
            String deployKey = "domain(" + i + ")";
            List<String> envList = getConfig().getList(deployKey + ".environment.[@name]");

            for (int j = 0; j < envList.size(); j++) {
                Environment env = new Environment();
                env.setName(envList.get(j));
                String envKey = deployKey + ".environment(" + j + ")";
                env.setHost(getConfig().getString(envKey + ".host"));
                env.setHostUsername(getConfig().getString(envKey + ".host-username"));
                env.setHostPassword(getConfig().getString(envKey + ".host-password"));
                env.setDbName(getConfig().getString(envKey + ".db-name"));
                env.setDbUsername(getConfig().getString(envKey + ".db-username"));
                env.setDbPassword(getConfig().getString(envKey + ".db-password"));
                domain.addEnv(env);
            }
            props.addDomain(domain);
        }

        return props;
    }
}
