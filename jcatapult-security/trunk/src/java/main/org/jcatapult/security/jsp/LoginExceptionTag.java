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
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.jcatapult.security.login.InvalidPasswordException;
import org.jcatapult.security.login.InvalidUsernameException;
import org.jcatapult.security.servlet.login.DefaultLoginExceptionHandler;

/**
 * <p> This is a JSP tag that will only execute it's body if there is a login exception and that exception matches one
 * of the boolean attributes of the tag that is set to true. </p> <p/> <p> This tag also allows you to throw your own
 * custom exceptions and then check for them using a String. The failure String must match the return value of the
 * getMessage of the thrown exception. </p>
 *
 * @author Brian Pontarelli
 */
public class LoginExceptionTag extends BodyTagSupport {
  private boolean username;
  private boolean password;
  private String failure;

  public void setUsername(boolean username) {
    this.username = username;
  }

  public void setPassword(boolean password) {
    this.password = password;
  }

  public void setFailure(String failure) {
    this.failure = failure;
  }

  @Override
  public int doStartTag() throws JspException {
    Throwable t = (Throwable) pageContext.findAttribute(DefaultLoginExceptionHandler.EXCEPTION_KEY);
    if (t != null) {
      if ((username && t instanceof InvalidUsernameException) ||
        (password && t instanceof InvalidPasswordException) ||
        (failure != null && failure.equals(t.getMessage()))) {
        return EVAL_BODY_INCLUDE;
      }
    }

    return SKIP_BODY;
  }
}