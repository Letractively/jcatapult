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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.domain.Attachment;
import org.jcatapult.email.domain.Email;

/**
 * {@inheritDoc}
 */
public class EmailCommandImpl implements EmailCommand {
    private final Map<String, Object> paramMap = new HashMap<String, Object>();
    private final String template;
    private final Email email;
    private final FreeMarkerEmailService freeMarkerEmailService;

    /**
     * Constructs a new instance that uses the given service as a callback and the email that was
     * pulled from the configuration.
     *
     * @param   template The name of the template.
     * @param   freeMarkerEmailService To send the email.
     * @param   email The email from the configuration.
     */
    EmailCommandImpl(String template, FreeMarkerEmailService freeMarkerEmailService, Email email) {
        this.template = template;
        this.freeMarkerEmailService = freeMarkerEmailService;
        this.email = email;
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand addTemplateParam(String name, Object value) {
        paramMap.put(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getTemplateParams() {
        return paramMap;
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand setSubject(String subject) {
        email.setSubject(subject);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getSubject() {
        return email.getSubject();
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand setTo(EmailAddress... toEmails) {
        email.setTo(toEmails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailAddress[] getTo() {
        return email.getTo();
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand setFrom(EmailAddress fromEmail) {
        email.setFrom(fromEmail);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailAddress getFrom() {
        return email.getFrom();
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand setBcc(EmailAddress... bccEmails) {
        email.setBcc(bccEmails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailAddress[] getBcc() {
        return email.getBcc();
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand setCc(EmailAddress... ccEmails) {
        email.setCc(ccEmails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public EmailAddress[] getCc() {
        return email.getCc();
    }

    /**
     * {@inheritDoc}
     */
    public EmailCommand addAttachments(Attachment... attachments) {
        for (Attachment attachment : attachments) {
            email.addAttachment(attachment);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment> getAttachments() {
        return email.getAttachments();
    }

    /**
     * {@inheritDoc}
     */
    public Future<Email> sendEmail() {
        return freeMarkerEmailService.sendEmail(template, email, paramMap);
    }
}
