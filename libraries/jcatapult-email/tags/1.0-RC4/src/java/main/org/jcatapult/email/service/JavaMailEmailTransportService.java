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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.configuration.Configuration;
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
 * The JavaMail session is created in the constructor and stored as a member
 * variable of the class. In order to create the session, the configuration
 * is used to load up the username and password for the SMTP server as well
 * as any other JavaMail configuration that is possible. Here are the configuration
 * keys that can be used.
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Description</th><th>Optional</th><th>Default if optional</th></tr>
 * <tr><td>jcatapult.email.username</td><td>The user name for the SMTP server.</td><td><b>false</b></td><td>N/A</td></tr>
 * <tr><td>jcatapult.email.password</td><td>The password for the SMTP server.</td><td><b>false</b></td><td>N/A</td></tr>
 * <tr><td>jcatapult.email.smtp-host</td><td>The SMTP host.</td><td>true</td><td>localhost</td></tr>
 * <tr><td>jcatapult.email.thread-pool.core-size</td><td>The initial size of the thread pool for asynchronous
 *  handling of the email sending.</td><td>true</td><td>1</td></tr>
 * <tr><td>jcatapult.email.thread-pool.maximum-size</td><td>The maximum size of the thread pool for asynchronous
 *  handling of the email sending.</td><td>true</td><td>5</td></tr>
 * <tr><td>jcatapult.email.thread-pool.keep-alive</td><td>The keep alive time (in milliseconds) to have threads
 *  stick around being idle prior to being thrown out.</td><td>true</td><td>500 milliseconds</td></tr>
 * </table>
 *
 * <p>
 * Besides these custom properties, you can also specify any properties that
 * are supported by the JavaMail specification. However, these properties
 * need to be prepended with email in order to be loaded. For example, if
 * you want to specify a default from address, you would add a property like
 * this:
 * </p>
 *
 * <pre>
 * email.mail.from = fred@example.com
 * </pre>
 *
 * <p>
 * If you are using an XML configuration it would look like this:
 * </p>
 *
 * <pre>
 * &lt;config>
 *   &lt;jcatapult>
 *     &lt;email>
 *       &lt;mail>
 *         &lt;from>fred@example.com&lt;/from>
 *       &lt;/mail>
 *     &lt;/email>
 *   &lt;/jcatapult>
 * &lt;/config>
 * </pre>
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
        Iterator<String> keysIter = configuration.getKeys();
        Properties props = new Properties();
        while (keysIter.hasNext()) {
            String key = keysIter.next();
            if (key.startsWith("jcatapult.email.")) {
                String emailKey = "mail.smtp." + key.substring("jcatapult.email.".length());
                String value = configuration.getString(key);
                props.setProperty(emailKey, value);
            }
        }

        // Set the auth
        final String username = configuration.getString("jcatapult.email.username");
        final String password = configuration.getString("jcatapult.email.password");
        Authenticator auth = null;
        if (username != null && password != null) {
            auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };

            props.put("mail.user", username);
            props.put("mail.smtp.auth", "true");
        }

        // Setup TLS
        boolean tls = configuration.getBoolean("jcatapult.email.tls", false);
        if (tls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        // Setup SSL
        boolean ssl = configuration.getBoolean("jcatapult.email.ssl", false);
        if (ssl) {
            String port = configuration.getString("jcatapult.email.port", "465");
            props.put("mail.smtp.ssl", "true");
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }

        // Set the SMTP host
        String smtpHost = configuration.getString("jcatapult.email.smtp-host");
        if (smtpHost != null) {
            props.setProperty("mail.smtp.host", smtpHost);
            props.setProperty("mail.host", smtpHost);
        }

        // Set the protocol for the JavaMail client
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.localhost", "localhost");

        session = Session.getInstance(props, auth);

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