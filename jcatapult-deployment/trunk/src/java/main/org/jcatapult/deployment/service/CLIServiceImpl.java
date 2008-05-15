package org.jcatapult.deployment.service;

import java.io.IOException;

import jline.ANSIBuffer;
import jline.Completor;
import jline.ConsoleReader;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class CLIServiceImpl implements CLIService {

    /**
     * Continues to ask the user a question until it gets a good answer. This uses the JLine command line
     * reading interface and currently uses the {@link BetterSimpleCompletor} for tab completion.
     *
     * @param   question The question to ask.
     * @param   message The message to give if the question is correctly answered.
     * @param   error The error to display if the question is incorrectly answered (left blank).
     * @param   defaultValue The default value to use (if the user leaves their answer blank.
     * @param   completor The JLine completor to use.
     * @return  The answer or the default value and never null or empty String.
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
