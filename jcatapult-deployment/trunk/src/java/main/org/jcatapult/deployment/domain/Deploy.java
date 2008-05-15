package org.jcatapult.deployment.domain;

import java.util.ArrayList;
import java.util.List;

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * Bean that represents a deploy descriptor in the deploy.xml file
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class Deploy implements Validatable {
    private String domain;

    private List<Environment> envs = new ArrayList<Environment>();

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<Environment> getEnvs() {
        return envs;
    }

    public void setEnvs(List<Environment> envs) {
        this.envs = envs;
    }

    public void addEnv(Environment env) {
        this.envs.add(env);
    }

    public ErrorList validate() {
        ErrorList errorList = new ErrorList();

        if (StringTools.isEmpty(domain)) {
            errorList.addError("The deploy.xml 'deploy' descriptor must define an attribute 'domain' with the value of the domain you wish to deploy to.\nex: <deploy domain=\"jcatapult\">");
        }

        if (envs.size() == 0) {
            errorList.addError("The deploy.xml 'deploy' descriptor content must contain at least one 'environment' descriptor.\nex: <environment type=\"staging\">");
        } else {
            for (Environment env : envs) {
                errorList.addAllErrors(env.validate());
            }
        }

        return errorList;
    }
}
