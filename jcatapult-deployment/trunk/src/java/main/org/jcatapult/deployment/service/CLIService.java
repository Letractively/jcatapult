package org.jcatapult.deployment.service;

import jline.Completor;
import com.google.inject.ImplementedBy;

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
     * @param message the message
     * @param error the error
     * @param defaultValue the default value
     * @param completor the completor to use
     * @return the input string
     */
    public String ask(String question, String message, String error, String defaultValue, Completor completor);
}
