/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jcatapult.struts.action;

import org.jcatapult.config.Configuration;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.servlet.annotation.HTTPMethod;
import org.jcatapult.struts.action.annotation.ActionName;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>
 * This class is the abstract base class for all actions.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class BaseAction extends ActionSupport {
    protected PersistenceService persistenceService;
    protected Configuration configuration;
    protected String method;
    protected boolean validationClearing = true;

    /**
     * Setter to inject services
     *
     * @param   persistenceService The {@link PersistenceService} that is used for JPA operations.
     * @param   configuration The Apache commons configuration instace. This will be injected by Guice
     *          into the subclass, so don't worry about figuring out how to grab this instance. Just
     *          add the Inject annotation to your action's constructor and Catapult/Guice will find
     *          the Configuration instance and inject it.
     */
    @Inject
    public void setServices(PersistenceService persistenceService, Configuration configuration) {
        this.persistenceService = persistenceService;
        this.configuration = configuration;
    }

    /**
     * @return  deprecated
     */
    public String getActionName() {
        return null;
    }

    /**
     * deprecated
     *
     * @param   actionName deprecated
     */
    @Deprecated
    public void setActionName(@ActionName String actionName) {
        // stubbed for deprecation
    }

    /**
     * @return  True if the HTTP request is a GET. If the method is never set, this will return true.
     */
    public boolean isGet() {
        return method == null || method.equals("GET");
    }

    /**
     * @return  True if the HTTP request is a POST. If the method is never set, this will return
     *          false.
     */
    public boolean isPost() {
        return method != null && method.equals("POST");
    }

    /**
     * Sets the HTTP method header of the request. This is also injected by JCatapult.
     *
     * @param   method The HTTP method header.
     */
    @Inject(optional = true)
    public void setMethod(@HTTPMethod String method) {
        this.method = method;
    }

    /**
     * <p>
     * This method is somewhat a hack. Struts 2 does validation during any type of request, including
     * GET requests. Therefore, if you want to use the same action to present a form and to handle
     * form submission, you need to ensure that validation is turned off for GET requests. This method
     * clears out any validation errors during a GET request. You can still add errors inside your
     * action method.
     * </p>
     *
     * <p>
     * You can turn off this behavior by calling the {@link #disableValidationClearing()} method and
     * turn it back on by calling the {@link #enableValidationClearing()} method.
     * </p>
     */
    @Override
    public void validate() {
        if (validationClearing && isGet()) {
            clearErrors();
        }
    }

    /**
     * Disables clearing of validation error messages when the request is a GET.
     */
    public void disableValidationClearing() {
        this.validationClearing = false;
    }

    /**
     * Enables clearing of validation error messages when the request is a GET.
     */
    public void enableValidationClearing() {
        this.validationClearing = true;
    }
}