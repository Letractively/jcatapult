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

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jcatapult.config.Configuration;
import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.EmailException;
import org.jcatapult.email.domain.Attachment;
import org.jcatapult.email.domain.Email;

import com.google.inject.Inject;
import net.java.lang.StringTools;

/**
 * <p>
 * This class implements the {@link org.jcatapult.email.service.EmailTransportService} interface using
 * the JavaMail API and a JavaMail session. This class should normally be
 * handled via Guice as a singleton so that it will only use a single session,
 * reducing the overhead.
 * </p>
 *
 * <p>
 * The JavaMail session is stored in the JNDI tree and configured via the JEE container.
 * This allows changes to be made in a much simpler and more transparent manner. It also
 * follows the same convention as the JDBC configuration for JPA.
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Description</th><th>Optional</th><th>Default if optional</th></tr>
 * <tr><td>jcatapult.email.jndi-name</td><td>The JNDI name under which the Mail session is stored (relative to the environment root of java:comp/env).</td><td><b>true</b></td><td>mail/Session</td></tr>
 * <tr><td>jcatapult.email.thread-pool.core-size</td><td>The initial size of the thread pool for asynchronous
 *  handling of the email sending.</td><td>true</td><td>1</td></tr>
 * <tr><td>jcatapult.email.thread-pool.maximum-size</td><td>The maximum size of the thread pool for asynchronous
 *  handling of the email sending.</td><td>true</td><td>5</td></tr>
 * <tr><td>jcatapult.email.thread-pool.keep-alive</td><td>The keep alive time (in milliseconds) to have threads
 *  stick around being idle prior to being thrown out.</td><td>true</td><td>500 milliseconds</td></tr>
 * </table>
 *
 * @author Brian Pontarelli
 */
public class JavaMailEmailTransportService implements EmailTransportService {
    private Session session;
    private ExecutorService executorService;

    /**
     * Constructs the transport service. This creates the JavaMail session and the thread pool
     * executor service for asynchronous handling of the email sending.
     *
     * @param   configuration Used to configure the JavaMail session and the thread pool. See the
     *          class comment for settings.
     */
    @Inject
    @SuppressWarnings({"unchecked"})
    public JavaMailEmailTransportService(Configuration configuration) {
        String name = configuration.getString("jcatapult.email.jndi-name", "mail/Session");
        try {
            InitialContext context = new InitialContext();
            session = (Session) context.lookup("java:comp/env/" + name);
        } catch (NamingException e) {
            throw new IllegalStateException("Invalid JNDI reference for the mail session [" + name +
                "]. This happens for three reasons: First, you are running the webapp and forgot to " +
                "uncomment the mail session in the deploy/tomcat/main/conf/context.xml file; Second, " +
                "you are running a unit test and forgot to call the EmailTestHelper.setup(this); " +
                "method; Third, you running an integration test and forgot to both call the " +
                "EmailTestHelper.setup(this) method AND add a mcok to the WebTestRunner like " +
                "withMock(EmailTransportService.class, EmailTestHelper.getService())");
        }

        // Create the thread pool executor
        int corePoolSize = configuration.getInt("jcatapult.email.thread-pool.core-size", 1);
        int maximumPoolSize = configuration.getInt("jcatapult.email.thread-pool.maximum-size", 5);
        int keepAlive = configuration.getInt("jcatapult.email.thread-pool.keep-alive", 500);
        executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAlive,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new RejectedExecutionHandler() {
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    throw new RejectedExecutionException("An email task was rejected.");
                }
            });
    }

    /**
     * {@inheritDoc}
     */
    public Future<Email> sendEmail(Email email) {
        try {
            return executorService.submit(new EmailRunnable(message(email),session), email);
        } catch (RejectedExecutionException ree) {
            throw new EmailException("Unable to submit the JavaMail message to the asynchronous handler " +
                "so that it can be processed at a later time. The email was therefore not sent.", ree);
        }
    }

    public void sendEmailLater(Email email) {
        try {
            executorService.execute(new EmailRunnable(message(email), session));
        } catch (RejectedExecutionException ree) {
            throw new EmailException("Unable to submit the JavaMail message to the asynchronous handler " +
                "so that it can be processed at a later time. The email was therefore not sent.", ree);
        }
    }

    private Message message(Email email) {
        try {
            // Define message
            Message message = new MimeMessage(session);
            EmailAddress from = email.getFrom();
            if (from == null) {
                throw new JCatapultEmailException("email message 'from' not set");
            }
            message.setFrom(new InternetAddress(from.getAddress(), from.getDisplay(), "UTF-8"));

            EmailAddress[] toList = email.getTo();
            for (EmailAddress to : toList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getAddress(), to.getDisplay(), "UTF-8"));
            }

            EmailAddress[] ccList = email.getCc();
            for (EmailAddress cc : ccList) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc.getAddress(), cc.getDisplay(), "UTF-8"));
            }

            EmailAddress[] bccList = email.getBcc();
            for (EmailAddress bcc : bccList) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc.getAddress(), bcc.getDisplay(), "UTF-8"));
            }

            if (message.getAllRecipients() == null || message.getAllRecipients().length == 0) {
                throw new JCatapultEmailException("email message must contain at least one CC, BCC, or To recipient");
            }

            String subject = email.getSubject();
            if (StringTools.isEmpty(subject)) {
                throw new JCatapultEmailException("email message 'subject' not set");
            }
            message.setSubject(subject);

            // Determine the email content type and if we need to include the text version
            String type = "alternative";
            boolean includeText = true;
            if (email.getAttachments().size() > 0) {
                type = "mixed";
                includeText = false;
            }

            // Get the text version if there is a template
            Multipart mp = new MimeMultipart(type);
            String text = email.getText();
            if (includeText && text != null) {
                BodyPart textPart = new MimeBodyPart();
                textPart.setText(text);
                mp.addBodyPart(textPart);
            }

            // Handle the HTML version if there is one
            String html = email.getHtml();
            if (html != null) {
                BodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(html, "text/html");
                mp.addBodyPart(htmlPart);
            }

            // Part two is attachment
            List<Attachment> attachments = email.getAttachments();
            for (Attachment attachment : attachments) {
                BodyPart attachPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(attachment.getAttachment(), attachment.getMime());
                attachPart.setDataHandler(new DataHandler(source));
                attachPart.setFileName(attachment.getName());
                mp.addBodyPart(attachPart);
            }

            // Set the multipart content
            message.setContent(mp);
            return message;
        } catch (MessagingException e) {
            throw new EmailException("An error occurred while trying to construct the JavaMail Message " +
                "object", e);
        } catch (UnsupportedEncodingException e) {
            throw new EmailException("Unable to create email addresses. The email was therefore not sent.", e);
        }
    }

    /**
     * The callable for handling async message sending.
     */
    public static class EmailRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(EmailRunnable.class.getName());
        private Message message;
        private Session session;

        public EmailRunnable(Message message, Session session) {
            this.message = message;
            this.session = session;
        }

        public void run() {
            try {
                logger.fine("Sending mail to JavaMail API");

                Transport transport = session.getTransport();
                transport.connect();
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                logger.fine("Finished JavaMail send");
            } catch (MessagingException e) {
                logger.log(Level.SEVERE, "Unable to send email via JavaMail", e);
                throw new EmailException("Unable to send email via JavaMail", e);
            }
        }
    }
}