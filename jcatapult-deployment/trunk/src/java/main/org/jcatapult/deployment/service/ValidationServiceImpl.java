package org.jcatapult.deployment.service;

import java.util.List;

import org.jcatapult.deployment.domain.DeploymentProperties;

import net.java.error.*;
import net.java.error.Error;
import net.java.validate.Validatable;

/**
 * Validation Service implementation for the Deployment Properties domain.
 * The validate method delegates to the {@link org.jcatapult.deployment.domain.DeploymentProperties#validate()} method
 * to perform validation.
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class ValidationServiceImpl<T extends Validatable> implements ValidationService<T> {

    /**
     * {@inheritDoc}
     */
    public String validate(T props) {
        ErrorList errorList = props.validate();

        // build a buffer with all the error messages.  this will be empty if no error messages exist
        StringBuffer errorBuffer = new StringBuffer();
        List<net.java.error.Error> errors = errorList.getAllErrors();
        for (Error error : errors) {
            errorBuffer.append(error.toString()).append("\n");
        }

        return errorBuffer.toString();
    }
}
