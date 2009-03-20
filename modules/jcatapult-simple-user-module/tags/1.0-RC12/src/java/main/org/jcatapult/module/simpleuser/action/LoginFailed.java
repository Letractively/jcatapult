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
 *
 */
package org.jcatapult.module.simpleuser.action;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.scope.annotation.Request;
import org.jcatapult.security.JCatapultSecurityException;

/**
 * <p>
 * This class handles a failed login. This takes the user back to
 * the login form and displays the errors.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Forward(page = "login.ftl")
public class LoginFailed {
    public String j_username;
    public String j_password;

    @Request()
    public JCatapultSecurityException jcatapult_security_login_exception;

    public String securityError;

    public String execute() {
        if (jcatapult_security_login_exception != null) {
            securityError = jcatapult_security_login_exception.getMessage();
        }
        
        return "success";
    }
}