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
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.scope.annotation.Session;

/**
 * <p>
 * This action sets up a scoped value.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
@Redirect(uri = "/scope/result")
public class Setup {
    @Session
    private String value;

    public String getValue() {
        return value;
    }

    public String execute() {
        value = "The number of milliseconds is " + System.currentTimeMillis();
        return "success";
    }
}