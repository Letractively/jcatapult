package org.jcatapult.deployment.domain;

import java.util.ArrayList;
import java.util.List;

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * Bean models a domain descriptor in the deploy xml configuration file
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class Domain implements Validatable {
    private String name;

    private List<Environment> envs = new ArrayList<Environment>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
