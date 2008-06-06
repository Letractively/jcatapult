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
package org.jcatapult.example.mvc.action;

import org.jcatapult.mvc.action.annotation.Action;

/**
 * <p>
 * This is a simple action that just displays a JSP with a
 * message.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Action
public class HelloWorld {
    private String message;

    public String getMessage() {
        return message;
    }

    public String execute() {
        message = "Hello World";
        return "success";
    }
}