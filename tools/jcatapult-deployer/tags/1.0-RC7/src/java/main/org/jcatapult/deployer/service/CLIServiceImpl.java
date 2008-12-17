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

import java.io.IOException;

import jline.ANSIBuffer;
import jline.Completor;
import jline.ConsoleReader;

/**
 * <p>JLine Implementation of the {@link org.jcatapult.deployer.service.CLIService}</p>
 *
 * @author jhumphrey
 */
public class CLIServiceImpl implements CLIService {

    /**
     * <p>Continues to ask the user a question until it gets a good answer. This uses the JLine command line
     * reading interface and currently uses the {@link BetterSimpleCompletor} for tab completion.</p>
     *
     * @param   question The question to ask.
     * @param   message The message to give if the question is correctly answered.
     * @param   error The error to display if the question is incorrectly answered (left blank).
     * @param   defaultValue The default value to use (if the user leaves their answer blank.
     * @param   completor The JLine completor to use.
     * @return The answer or the default value and never null or empty String.
     */
    public String ask(String question, String message, String error, String defaultValue, Completor completor) {
        String answer = "";
        while (answer.length() == 0) {
            try {
                ConsoleReader reader = new ConsoleReader();
                reader.addCompletor(completor);
                ANSIBuffer buffer = new ANSIBuffer();
                buffer.bold(question).blue(" [" + (defaultValue == null ? "" : defaultValue) + "]").bold(": ");
                answer = reader.readLine(buffer.toString(reader.getTerminal().isANSISupported())).trim();
            } catch (IOException e) {
                System.err.println("Error getting user input");
                e.printStackTrace();
                System.exit(1);
            }

            if (answer.length() == 0 && defaultValue != null) {
                answer = defaultValue;
            }

            if (answer.length() == 0) {
                System.out.println(error);
            } else {
                System.out.println(message + "[" + answer + "]");
            }
        }

        return answer;
    }
}
