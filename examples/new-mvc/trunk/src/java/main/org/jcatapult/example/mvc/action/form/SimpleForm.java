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
package org.jcatapult.example.mvc.action.form;

import org.jcatapult.example.mvc.domain.SimpleFormBean;
import org.jcatapult.mvc.action.annotation.Action;

/**
 * <p>
 * This class is a simple form submission.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
public class SimpleForm {
    public SimpleFormBean simpleFormBean;

    public String execute() {
        if (simpleFormBean != null) {
            System.out.println("Hello there " + simpleFormBean.getFirstName() + " " + simpleFormBean.getLastName());
        }
        
        return "success";
    }
}