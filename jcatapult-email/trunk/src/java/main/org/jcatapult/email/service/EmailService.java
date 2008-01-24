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
package org.jcatapult.email.service;

import java.util.Map;
import java.util.concurrent.Future;

import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.domain.Attachment;
import org.jcatapult.email.domain.Email;

import com.google.inject.ImplementedBy;
import net.java.util.NameValuePairChain;

/**
 * <p>
 * This interface defines how to send emails in a simple and
 * templatized manner.
 * </p>
 *
 * @author Brian Pontarelli and James Humphrey
 */
@ImplementedBy(FreeMarkerEmailService.class)
public interface EmailService {
    /**
     * Called to build the email using the specified template as the email body.
     * The {@link EmailBuilder} returned provides the interface necessary to configure
     *
     * Here's an example using the template below named 'hello':
     *
     * <pre>
     * Hello ${name},
     *
     * Thanks for buying all that cool stuff.
     * </pre>
     *
     * <p>
     * You would call this method like this:
     * </p>
     *
     * <pre>
     * buildEmail("hello").addTemplateParam("name", "Joe Blow").setTo("joe@blow.com").setFrom("info@example.com").sendEmail();
     * </pre>
     *
     * @param   template (Required) The name of the template. The implementation will dictate the type
     *          of template and how they are stored.
     * @return The name value pair chain.
     */
    EmailBuilder buildEmail(String template);

    /**
     * Called to send the email
     *
     * @param emailBuilder {@link org.jcatapult.email.service.EmailService.EmailBuilder}
     * @return a future response from sending the email message
     */
    Future<Email> sendEmail(EmailBuilder emailBuilder);

    /**
     * Interface for the EmailBuilder object.  This is a service level
     * object only and is only used within the EmailService interface.
     */
    public static interface EmailBuilder {

        /**
         * Adds template params for token replacement within the template
         *
         * @param name the param name
         * @param value the param value
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder addTemplateParam(String name, Object value);

        /**
         * Sets the email subject
         *
         * @param subject the email subject
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder setSubject(String subject);

        /**
         * Method to set a list of email to addresses.  This method assumes that the to display is equal to the
         * to address.
         *
         * @param toEmails list of to email to send the email to (required)
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder setToEmails(EmailAddress... toEmails);

        /**
         * Sets the from email address
         *
         * @param fromEmail the from email
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder setFromEmail(EmailAddress fromEmail);

        /**
         * vararg method to add blind carbon copies
         *
         * @param bccEmails the email blind carbon copy
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder setBCCEmails(EmailAddress... bccEmails);

        /**
         * vararg method to add email carbon copies
         *
         * @param ccEmails the email carbon copy
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder setCCEmails(EmailAddress... ccEmails);

        /**
         * Vararg method to add attachments to the email
         *
         * @param attachments list of email attachments
         * @return {@link EmailBuilder} the Email Builder
         */
        EmailBuilder addAttachments(Attachment... attachments);

        /**
         * Returns a map of all the template params
         *
         * @return the template param map
         */
        Map<String, Object> getTemplateParamMap();

        /**
         * Returns the email built by this EmailBuilder
         *
         * @return the built email
         */
        Email getEmail();
    }
}