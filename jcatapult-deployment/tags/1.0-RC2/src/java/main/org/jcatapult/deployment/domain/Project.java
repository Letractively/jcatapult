package org.jcatapult.deployment.domain;

import java.io.File;

import net.java.error.ErrorList;
import net.java.lang.StringTools;
import net.java.validate.Validatable;

/**
 * Bean to represent a project xml file.  specifically, the project name and its root directory
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class Project implements Validatable {
    private String name;
    private File dir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public ErrorList validate() {
        ErrorList errorList = new ErrorList();

        if (StringTools.isEmpty(name)) {
            errorList.addError("Project name must be defined in the project.xml");
        }

        return errorList;
    }
}
