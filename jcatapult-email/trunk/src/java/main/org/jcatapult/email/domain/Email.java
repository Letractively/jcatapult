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
package org.jcatapult.email.domain;

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.domain.Attachment;

import net.java.util.CollectionTools;

/**
 * <p>
 * This class is an abstraction of a simple email message.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Email {
    private String subject;
    private EmailAddress from;
    private EmailAddress replyTo;
    private List<EmailAddress> to = new ArrayList<EmailAddress>();
    private List<EmailAddress> cc = new ArrayList<EmailAddress>();
    private List<EmailAddress> bcc = new ArrayList<EmailAddress>();
    private String text;
    private String html;
    private List<Attachment> attachments = new ArrayList<Attachment>();

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailAddress getFrom() {
        return from;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public EmailAddress getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(EmailAddress replyTo) {
        this.replyTo = replyTo;
    }

    public EmailAddress[] getTo() {
        return to.toArray(new EmailAddress[to.size()]);
    }

    public void setTo(EmailAddress... to) {
        this.to = CollectionTools.list(to);
    }

    /**
     * Adds a to EmailAddress address to the list.
     *
     * @param   to The to EmailAddress address.
     */
    public void addTo(EmailAddress to) {
        this.to.add(to);
    }

    public EmailAddress[] getCc() {
        return cc.toArray(new EmailAddress[cc.size()]);
    }

    public void setCc(EmailAddress... cc) {
        this.cc = CollectionTools.list(cc);
    }

    /**
     * Adds a cc EmailAddress address to the list.
     *
     * @param   cc The cc EmailAddress address.
     */
    public void addCc(EmailAddress cc) {
        this.cc.add(cc);
    }

    public EmailAddress[] getBcc() {
        return bcc.toArray(new EmailAddress[bcc.size()]);
    }

    public void setBcc(EmailAddress... bcc) {
        this.bcc = CollectionTools.list(bcc);
    }

    /**
     * Adds a bcc EmailAddress address to the list.
     *
     * @param   bcc The bcc EmailAddress address.
     */
    public void addBcc(EmailAddress bcc) {
        this.bcc.add(bcc);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }
}