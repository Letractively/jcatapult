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
import java.util.HashMap;

import org.jcatapult.email.domain.Email;
import org.jcatapult.email.domain.Attachment;
import org.jcatapult.email.EmailException;
import org.jcatapult.domain.contact.EmailAddress;
import org.apache.commons.configuration.Configuration;

/**
 * {@inheritDoc}
 */
public class EmailBuilderImpl implements EmailService.EmailBuilder {

    private Map<String, Object> paramMap = new HashMap<String, Object>();
    private Email email = new Email();

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder addTemplateParam(String name, Object value) {
        paramMap.put(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder setSubject(String subject) {
        email.setSubject(subject);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder setToEmails(EmailAddress... toEmails) {
        email.setTo(toEmails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder setFromEmail(EmailAddress fromEmail) {
        email.setFrom(fromEmail);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder setBCCEmails(EmailAddress... bccEmails) {
        email.setBcc(bccEmails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder setCCEmails(EmailAddress... ccEmails) {
        email.setCc(ccEmails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailService.EmailBuilder addAttachments(Attachment... attachments) {
        for (Attachment attachment : attachments) {
            email.addAttachment(attachment);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getTemplateParamMap() {
        return paramMap;
    }

    /**
     * {@inheritDoc}
     */
    public Email getEmail() {
        return email;
    }
}
