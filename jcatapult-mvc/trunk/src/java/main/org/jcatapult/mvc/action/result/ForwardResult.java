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
package org.jcatapult.mvc.action.result;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.result.annotation.Forward;

/**
 * <p>
 * This result performs a servlet forward to a JSP or renders a FreeMarker
 * template depending on the extension of the page.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ForwardResult implements Result<Forward> {
    /**
     * {@inheritDoc}
     */
    public void execute(Forward forward, ActionInvocation invocation, HttpServletRequest request,
        HttpServletResponse response)
    throws IOException, ServletException {
        String page = forward.page();
        if (page.endsWith(".jsp")) {
            if (!page.startsWith("/")) {
                page = DefaultResultInvocationProvider.DIR + "/" + invocation.actionURI() + "/" + page;
            }
            
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(page);
            requestDispatcher.forward(request, response);
        } else {
            throw new RuntimeException("Not supported yet");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<Forward> annotation() {
        return Forward.class;
    }
}