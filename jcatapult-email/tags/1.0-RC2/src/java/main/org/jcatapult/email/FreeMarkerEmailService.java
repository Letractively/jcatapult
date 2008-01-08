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
package org.jcatapult.email;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.container.FreeMarkerContainerTemplateLoader;
import org.jcatapult.domain.contact.EmailAddress;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Template;
import net.java.util.NameValuePairChain;

/**
 * <p>
 * This class implements the {@link EmailService} interface and generates emails
 * using FreeMarker. This service is thread safe from the stand point that it
 * doesn't store anything that is not thread safe.
 * </p>
 *
 * <p>
 * This class takes a configuration instance that can be used to configure the
 * handling of the FreeMarker templates that will be used to generate the emails.
 * Be default this class looks in the web applications <b>/WEB-INF/email</b>
 * directory for the templates, but this can be configured. The template name
 * passed in is assumed to be the same as the name of the FreeMarker file without
 * the <b>ftl</b> file extension. For example, if you have a FreeMarker template
 * named <b>foo.ftl</b>, you would pass into this class the string <b>foo</b>.
 * This makes it easier to assign custom configuration to a specific template.
 * The configuration for each template is documented later.
 * <p>
 *
 * <p>
 * There are a number of configuration parameters that can be specified to control
 * the behavior of FreeMarker. These are:
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Description</th><th>Optional</th><th>Default if optional</th></tr>
 * <tr><td>email.templates.location</td><td>The location on disk of the email templates. This only
 *  works when the web application is deployed in exploded form. Otherwise the FreeMarker templates
 *  are searched for via the classpath.</td><td>Yes</td><td>/WEB-INF/email</td></tr>
 * <tr><td>email.templates.cache</td><td>A boolean that determines if the email templates should
 *  be cached by FreeMarker.</td><td>Yes</td><td>false</td></tr>
 * <tr><td>email.templates.check-interval</td><td>The number of seconds to check if the FreeMarker
 *  template has been modified. If caching is turned off this value is ignored.</td><td>Yes</td>
 *  <td>N/A</td></tr>
 * </table>
 *
 * <p>
 * After the templates are found, this class constructs an {@link Email} instance
 * by using any information passed into the class as well as any information from
 * the configuration. This makes it easy to set the default <b>from</b> field and
 * other attributes of the email. Here are the configuration paramters that can be
 * set. Note that the &lt;template> is replaced by the template name passed to this
 * class. Also note that this class first uses values passed in and then the values
 * from the configuration.
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Description</th><th>Optional</th><th>Default if optional</th></tr>
 * <tr><td>email.&lt;template>.from</td><td>This specifies from address for the email.</td>
 *  <td>Yes</td><td>The value passed in</td></tr>
 * <tr><td>email.&lt;template>.to</td><td>This specifies to address for the email.</td>
 *  <td>Yes</td><td>The value passed in</td></tr>
 * <tr><td>email.&lt;template>.subject</td><td>This specifies subject for the email.</td>
 *  <td>Yes</td><td>The value passed in</td></tr>
 * </table>
 *
 * @author Brian Pontarelli
 */
@Singleton
public class FreeMarkerEmailService implements EmailService {
    private static final Logger logger = Logger.getLogger(FreeMarkerEmailService.class.getName());
    private EmailTransportService emailTransportService;
    private Configuration configuration;
    private freemarker.template.Configuration freeMarkerConfiguration = new freemarker.template.Configuration();

    /**
     * Constructs a FreeMarkerEmailService. The transport given is used to send the emails and the
     * configuration given is used to control FreeMarker behavior.
     *
     * @param   emailTransportService Used to send emails.
     * @param   configuration Used to control FreeMarker.
     * @param   containerResolver The ContainerResolver that is used to find email templates in web applications.
     * @param   defaultLocation The defaultLocation of the email templates to use. This is passed to the container
     *          path resolver.
     * @throws  Exception If FreeMarker initialization failed.
     */
    @Inject
    public FreeMarkerEmailService(EmailTransportService emailTransportService,
            Configuration configuration, ContainerResolver containerResolver,
            @Named("jcatapult.email.templates.location") String defaultLocation)
    throws Exception {
        this.emailTransportService = emailTransportService;
        this.configuration = configuration;

        // Setup FreeMarker
        String templatesLocation = configuration.getString("email.templates.location");
        if (templatesLocation == null) {
            templatesLocation = defaultLocation;
        }

        ClassTemplateLoader ctl = new ClassTemplateLoader(this.getClass(), templatesLocation);
        FreeMarkerContainerTemplateLoader watl = new FreeMarkerContainerTemplateLoader(containerResolver, templatesLocation);
        MultiTemplateLoader loader = new MultiTemplateLoader(new TemplateLoader[]{watl, ctl});
        freeMarkerConfiguration.setTemplateLoader(loader);

        boolean cache = configuration.getBoolean("email.templates.cache", false);
        if (!cache) {
            freeMarkerConfiguration.setTemplateUpdateDelay(Integer.MAX_VALUE);
        } else {
            int checkInterval = configuration.getInt("email.templates.check-interval", 2);
            freeMarkerConfiguration.setTemplateUpdateDelay(checkInterval);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NameValuePairChain<String, Object, Future<Email>> sendConfiguredEmail(String template, String... to) {
        return sendEmail(template, null, null, to);
    }

    /**
     * {@inheritDoc}
     */
    public NameValuePairChain<String, Object, Future<Email>> sendEmail(String template, String subject,
            String from, String... to) {
        Email email = new Email();
        if (from == null) {
            from = configuration.getString("email." + template + ".from");
            if (from == null) {
                throw new EmailException("The [from] parameter was not passed into the EmailService and it was " +
                    "not in the configuration under the key [email." + template + ".from]");
            }
        }
        email.setFrom(new EmailAddress(from, from));

        if (to == null || to.length == 0) {
            to = configuration.getStringArray("email." + template + ".to");
            if (to == null) {
                throw new EmailException("The [to] parameter was not passed into the EmailService and it was " +
                    "not in the configuration under the key [email." + template + ".to]");
            }
        }

        for (String toAddy : to) {
            email.addTo(new EmailAddress(toAddy, null));
        }

        if (subject == null) {
            subject = configuration.getString("email." + template + ".subject");
            if (subject == null) {
                throw new EmailException("The [subject] parameter was not passed into the EmailService and it was " +
                    "not in the configuration under the key [email." + template + ".subject]");
            }
        }
        email.setSubject(subject);

        Map<String, Object> parameters = new HashMap<String, Object>();
        Callback callback = new Callback(parameters, email, template);
        return new NameValuePairChain<String, Object, Future<Email>>(parameters, callback);
    }

    /**
     * Processes the FreeMarker template.
     *
     * @param   templateName The name of the template file to process.
     * @param   parameters The parameters that are passed to the template.
     * @return  The String result of the processing the template.
     * @throws  EmailException If the template couldn't be processed.
     */
    protected String callTemplate(String templateName, Map<String, Object> parameters) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Invoking FreeMarker email template [" + templateName + "] with parameters " +
                parameters);
        }

        Template template;
        try {
            template = freeMarkerConfiguration.getTemplate(templateName);
        } catch (IOException e) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Error loading template [" + templateName + "]", e);
            }

            return null;
        }

        try {
            StringWriter writer = new StringWriter();
            template.process(parameters, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new EmailException("Unable to process FreeMarker template [" + templateName + "]", e);
        }
    }

    /**
     * The NameValuePairChain callback. This callback collects all the parameters and
     * then passes those parameters to a FreeMarker template. If then hands the Email
     * instance off to the {@link EmailTransportService} for sending.
     */
    public class Callback implements NameValuePairChain.Callback<Future<Email>> {
        private Map<String, Object> map;
        private Email email;
        private String template;

        public Callback(Map<String, Object> map, Email email, String template) {
            this.map = map;
            this.email = email;
            this.template = template;
        }

        public Future<Email> done() {
            // Get the text version if there is a template
            String text = callTemplate(template + "-text.ftl", map);
            if (text != null) {
                email.setText(text);
            }

            // Handle the HTML version if there is one
            String html = callTemplate(template + "-html.ftl", map);
            if (html != null) {
                email.setHtml(html);
            }

            return emailTransportService.sendEmail(email);
        }
    }
}