package org.jcatapult.deployment.service;

import com.google.inject.ImplementedBy;

/**
 * Service Interface for validating beans of type T
 *
 * User: jhumphrey
 * Date: May 15, 2008
 */
@ImplementedBy(ValidationServiceImpl.class)
public interface ValidationService<T> {

    /**
     * Called to validate a bean of type T
     *
     * @param bean the bean to validate
     * @return an error message string.  Should return null or empty string if no errors exist.
     */
    public String validate(T bean);
}
