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

import org.apache.commons.configuration.Configuration;
import org.apache.struts2.ServletActionContext;
import org.jcatapult.persistence.PersistenceService;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
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
     * @return  Returns the action name. This is often useful when handling different requests to
     *          the same method.
     */
    public String getActionName() {
        return ActionContext.getContext().getName();
    }

    /**
     * @return  True if the HTTP request is a GET.
     */
    public boolean isGet() {
        return ServletActionContext.getRequest().getMethod().equals("GET");
    }

    /**
     * @return  True if the HTTP request is a POST.
     */
    public boolean isPost() {
        return ServletActionContext.getRequest().getMethod().equals("POST");
    }
}