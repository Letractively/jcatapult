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

import java.util.Set;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.guice.GuiceContainer;

import com.google.inject.Inject;

/**
 * <p>
 * This is a JSP tag that will only execute it's body if the current user exists and
 * has one or more of the given roles. The roles are a comma separated list.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class HasRoleTag extends BodyTagSupport {
    private UserAdapter userAdapter;
    private String[] roles;

    @Inject
    public void setUserAdapter(UserAdapter userAdapter) {
        this.userAdapter = userAdapter;
    }

    public void setRoles(String roles) {
        this.roles = roles.split("\\W*,\\W*");
    }

    @Override
    public int doStartTag() throws JspException {
        GuiceContainer.getInjector().injectMembers(this);
        if (roles == null || roles.length == 0) {
            throw new JspException("You must supply a list of roles to the hasRole tag.");
        }

        Object user = SecurityContext.getCurrentUser();
        if (user == null) {
            return SKIP_BODY;
        }

        Set<String> userRoles = userAdapter.getRoles(user);
        if (userRoles == null) {
            return SKIP_BODY;
        }

        for (String role : roles) {
            if (userRoles.contains(role)) {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}