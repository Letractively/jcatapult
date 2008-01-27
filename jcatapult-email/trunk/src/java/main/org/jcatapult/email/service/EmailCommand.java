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
import java.util.List;
import java.util.concurrent.Future;

import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.domain.Attachment;
import org.jcatapult.email.domain.Email;

/**
 * <p>
 * Interface for the EmailCommand object.  This is a service level
 * object only and is only used within the EmailService interface in order
 * to collect the necessary information about an email and then send it.
 * Although it is implementaiton independent, in most cases this
 * instance will be prepopulated with configured email information
 * and then allow the caller to override whatever data they need.
 * </p>
 */
public interface EmailCommand {
    /**
     * Adds template params for token replacement within the template
     *
     * @param   name the param name
     * @param   value the param value
     * @return  This instance.
     */
    EmailCommand addTemplateParam(String name, Object value);

    /**
     * Returns a map of all the template params.
     *
     * @return the template param map
     */
    Map<String, Object> getTemplateParams();

    /**
     * Sets the email subject
     *
     * @param   subject the email subject
     * @return  This instance.
     */
    EmailCommand setSubject(String subject);

    /**
     * @return  The previous set subject or null.
     */
    String getSubject();

    /**
     * Method to set a list of email to addresses.  This method assumes that the to display is equal to the
     * to address.
     *
     * @param   to The list of to email to send the email to (required).
     * @return  This instance.
     */
    EmailCommand setTo(EmailAddress... to);

    /**
     * @return  The previously set to addresses or null.
     */
    EmailAddress[] getTo();

    /**
     * Sets the from email address
     *
     * @param   from The from email
     * @return  This instance.
     */
    EmailCommand setFrom(EmailAddress from);

    /**
     * @return  The previously set from address or null.
     */
    EmailAddress getFrom();

    /**
     * vararg method to add blind carbon copies
     *
     * @param   bcc The email blind carbon copy
     * @return  This instance.
     */
    EmailCommand setBcc(EmailAddress... bcc);

    /**
     * @return  The previously set bcc address or null.
     */
    EmailAddress[] getBcc();

    /**
     * vararg method to add email carbon copies
     *
     * @param   cc The carbon copy email addresses.
     * @return  This instance.
     */
    EmailCommand setCc(EmailAddress... cc);

    /**
     * @return  The previously set cc addresses or null.
     */
    EmailAddress[] getCc();

    /**
     * Vararg method to add attachments to the email
     *
     * @param   attachments The list of email attachments.
     * @return  This instance.
     */
    EmailCommand addAttachments(Attachment... attachments);

    /**
     * @return  The previously set list of attachments.
     */
    List<Attachment> getAttachments();

    /**
     * Sends the email.
     *
     * @return  A Future that represents the sending operation which might have already happened or
     *          will happen in the future.
     */
    Future<Email> sendEmail();
}