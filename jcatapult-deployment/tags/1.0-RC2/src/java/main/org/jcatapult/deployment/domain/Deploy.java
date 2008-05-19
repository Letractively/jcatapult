package org.jcatapult.deployment.domain;

import java.util.ArrayList;
import java.util.List;

import net.java.error.ErrorList;
import net.java.validate.Validatable;

/**
 * Bean to model deployment properties defined in the deploy xml configuration file
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class Deploy implements Validatable {
    List<Domain> domains = new ArrayList<Domain>();

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    /**
     * Adds one {@link Domain} to the deploys list
     *
     * @param domain {@link Domain}
     */
    public void addDomain(Domain domain) {
        this.domains.add(domain);
    }

    /**
     * Called to validate bean properties.  returns a list of errors or empty list
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
