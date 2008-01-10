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
package org.jcatapult.security.jsp;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.persistence.EntityManager;

import org.jcatapult.security.SecurityContext;
import org.jcatapult.jpa.EntityManagerContext;

/**
 * <p>
 * This is a JSP tag that fetches and possibly updates the user from the
 * database as well as re-attaches the instance. By default this does not
 * reload and uses a variable name of <strong>user</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class UserTag extends TagSupport {
    private boolean reload;
    private String var = "user";

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public int doStartTag() throws JspException {
        Object user = SecurityContext.getCurrentUser();
        if (reload) {
            EntityManager entityManager = EntityManagerContext.get();
            user = entityManager.merge(user);
            SecurityContext.update(user);
        }

        pageContext.setAttribute(var, user);
        return super.doStartTag();
    }
}