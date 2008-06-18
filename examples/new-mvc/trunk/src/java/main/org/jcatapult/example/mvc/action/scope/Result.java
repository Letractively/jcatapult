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
package org.jcatapult.example.mvc.action.scope;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.scope.annotation.Session;

/**
 * <p>
 * This action is the result, but also should receive the scoped value.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
public class Result {
    // This is set into the action by the JCatapult MVC as long as the Setup action has been
    // invoked
    @Session
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String execute() {
        System.out.println("The value is " + value);
        return "success";
    }
}