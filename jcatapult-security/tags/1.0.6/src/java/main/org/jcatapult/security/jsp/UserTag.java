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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.jcatapult.security.SecurityContext;

/**
 * <p> This is a JSP tag that places the user object into the PageContext under the variable name given. If the variable
 * name is not set, the default is <code>user</code>. </p>
 *
 * @author Brian Pontarelli
 */
public class UserTag extends TagSupport {
  private String var = "user";

  public void setVar(String var) {
    this.var = var;
  }

  @Override
  public int doEndTag() throws JspException {
    Object user = SecurityContext.getCurrentUser();
    pageContext.setAttribute(var, user);
    return SKIP_BODY;
  }
}