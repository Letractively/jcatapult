package org.jcatapult.deployment.domain;

import net.java.validate.Validatable;
import net.java.error.ErrorList;
import net.java.lang.StringTools;

/**
 * Bean to represent a project xml file.  specifically, the project name and version contained within
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class Project implements Validatable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ErrorList validate() {
        ErrorList errorList = new ErrorList();

        if (StringTools.isEmpty(name)) {
            errorList.addError("Project name must be defined in the project.xml");
        }

        return errorList;
    }
}
