/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
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

package org.jcatapult.deployer.service;

import com.google.inject.ImplementedBy;
import jline.Completor;

/**
 * <p>Interface service for command line interaction</p>
 *
 * @author jhumphrey
 */
@ImplementedBy(CLIServiceImpl.class)
public interface CLIService {

    /**
     * <p>Called to ask the cli for information</p>
     *
     * @param question the question
     * @param message the message to display after input is received
     * @param error the error messaged displayed if an invalid option is inputted
     * @param defaultValue the default value to select if no option is chosen
     * @param completor the completor to use
     * @return the input string
     */
    public String ask(String question, String message, String error, String defaultValue, Completor completor);
}
