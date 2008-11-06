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

import java.util.Properties;
import java.util.concurrent.Future;
import javax.mail.Session;

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.domain.Attachment;
import org.jcatapult.email.domain.Email;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.java.naming.MockJNDI;

/**
 * <p>
 * This class tests the JavaMailEmailTransportService.
 * </p>
 *
 * @author Brian Pontarelli
 */

public class JavaMailEmailTransportServiceTest {
    @BeforeClass
    public static void setupJNDI() {
        // Set the auth
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "localhost");
        props.setProperty("mail.host", "localhost");

        // Set the protocol for the JavaMail client
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.localhost", "localhost");

        Session session = Session.getInstance(props, null);
        MockJNDI jndi = new MockJNDI();
        jndi.activate();
        jndi.bind("java:comp/env/mail/Session", session);
    }

    @Test
    public void testSendEmail() throws Exception {
        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getString("jcatapult.email.jndi-name", "mail/Session")).andReturn("mail/Session");
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.core-size", 1)).andReturn(1);
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.maximum-size", 5)).andReturn(5);
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.keep-alive", 500)).andReturn(500);
        EasyMock.replay(config);

        JavaMailEmailTransportService service = new JavaMailEmailTransportService(config);
        Email email = new Email();
        email.setFrom(new EmailAddress("jhumphrey@jcatapult.org"));
        email.setTo(new EmailAddress("bpontarelli@jcatapult.org"));
        email.setSubject("Test email");
        email.setText("text");
        email.setHtml("<html><body><h3>html</h3></body></html>");
        Future<Email> future = service.sendEmail(email);
        Assert.assertNotNull(future);
        future.get();
    }

    @Test
    public void testSendEmailWithAttachments() throws Exception {
        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getString("jcatapult.email.jndi-name", "mail/Session")).andReturn("mail/Session");
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.core-size", 1)).andReturn(1);
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.maximum-size", 5)).andReturn(5);
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.keep-alive", 500)).andReturn(500);
        EasyMock.replay(config);

        JavaMailEmailTransportService service = new JavaMailEmailTransportService(config);
        Email email = new Email();
        email.setFrom(new EmailAddress("unittests@jcatapult.org"));
        email.setTo(new EmailAddress("unittests@jcatapult.org"));
        email.setSubject("Test email");
        email.setText("text");
        email.setHtml("<html><body><h3>html</h3></body></html>");
        email.addAttachment(new Attachment("test.txt", "text/plain", "Hello world".getBytes()));
        Future<Email> future = service.sendEmail(email);
        Assert.assertNotNull(future);
        future.get();
    }

    @Ignore
    public void testSendEmailRemote() throws Exception {
        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getString("jcatapult.email.jndi-name", "mail/Session")).andReturn("mail/Session");
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.core-size", 1)).andReturn(1);
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.maximum-size", 5)).andReturn(5);
        EasyMock.expect(config.getInt("jcatapult.email.thread-pool.keep-alive", 500)).andReturn(500);
        EasyMock.replay(config);

        JavaMailEmailTransportService service = new JavaMailEmailTransportService(config);
        Email email = new Email();
        email.setFrom(new EmailAddress("unittests@jcatapult.org"));
        email.setTo(new EmailAddress("unittests@jcatapult.org"));
        email.setSubject("Test email");
        email.setText("text");
        email.setHtml("html");
        Future<Email> future = service.sendEmail(email);
        Assert.assertNotNull(future);
        future.get();
    }
}