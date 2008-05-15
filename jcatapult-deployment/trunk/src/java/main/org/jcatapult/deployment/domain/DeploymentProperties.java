package org.jcatapult.deployment.domain;

import java.util.ArrayList;
import java.util.List;

import net.java.error.ErrorList;
import net.java.validate.Validatable;

/**
 * Bean to represent deployment properties defined in the deploy.xml
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class DeploymentProperties implements Validatable {
    List<Deploy> deploys = new ArrayList<Deploy>();

    public List<Deploy> getDeploys() {
        return deploys;
    }

    public void setDeploys(List<Deploy> deploys) {
        this.deploys = deploys;
    }

    /**
     * Adds one {@link org.jcatapult.deployment.domain.Deploy} to the deploys list
     *
     * @param deploy {@link org.jcatapult.deployment.domain.Deploy}
     */
    public void addDeploy(Deploy deploy) {
        this.deploys.add(deploy);
    }

    /**
     * Called to validate bean properties.  returns a list of errors or empty list
     *
     * @return an error list.  empty if no errors exist
     */
    public ErrorList validate() {
        ErrorList errorList = new ErrorList();
        if (deploys.size() == 0) {
            errorList.addError("The deploy.xml must contain at least one 'deploy' descriptor.\nex: <deploy domain=\"jcatapult\">");
        } else {
            for (Deploy deploy : deploys) {
                errorList.addAllErrors(deploy.validate());
            }
        }

        return errorList;
    }
}
