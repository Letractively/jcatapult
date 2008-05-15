package org.jcatapult.deployment.service;

import com.google.inject.ImplementedBy;
import jline.Completor;

/**
 * Interface service for command line interaction
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
@ImplementedBy(JLineCLIService.class)
public interface CLIService {

    /**
     * Called to ask the cli for information
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
