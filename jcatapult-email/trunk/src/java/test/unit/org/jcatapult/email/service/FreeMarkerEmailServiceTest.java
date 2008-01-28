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

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.domain.Email;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class tests the FreeMarker email service.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailServiceTest {
    @Test
    public void testSendEmailClassPath() throws Exception {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-text_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-text_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-text_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-text_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-text.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-text.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-html_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-html_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-html_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-html_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-html.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-html.ftl")).andReturn(null);
        EasyMock.replay(containerResolver);

        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getString("email.templates.location")).andReturn("/org/jcatapult/email");
        EasyMock.expect(config.getBoolean("email.templates.cache", false)).andReturn(true);
        EasyMock.expect(config.getInt("email.templates.check-interval", 2)).andReturn(1);
        EasyMock.expect(config.getStringArray("email.test-template.to")).andReturn(null);
        EasyMock.expect(config.getString("email.test-template.from")).andReturn(null);
        EasyMock.expect(config.getString("email.test-template.subject")).andReturn(null);
        EasyMock.expect(config.getStringArray("email.test-template.cc")).andReturn(null);
        EasyMock.expect(config.getStringArray("email.test-template.bcc")).andReturn(null);
        EasyMock.replay(config);

        MockEmailTransportService transport = new MockEmailTransportService();
        FreeMarkerEmailService service = new FreeMarkerEmailService(transport, config, containerResolver, "/WEB-INF/email");
        service.sendEmail("test-template").andCC(new EmailAddress("from@example.com")).
            andBCC(new EmailAddress("from@example.com")).withSubject("test subject").
            from(new EmailAddress("from@example.com")).to(new EmailAddress("to@example.com")).
            withTemplateParam("key1", "value1").now();
        Assert.assertEquals("test subject", transport.email.getSubject());
        Assert.assertEquals("from@example.com", transport.email.getFrom().getAddress());
        Assert.assertEquals("to@example.com", transport.email.getTo()[0].getAddress());
        Assert.assertEquals("Text value1", transport.email.getText());
        Assert.assertEquals("HTML value1", transport.email.getHtml());
        EasyMock.verify(containerResolver);
        EasyMock.verify(config);
    }

    @Test
    public void testSendEmailWebApp() throws Exception {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("/WEB-INF/email/test-template-text_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/WEB-INF/email/test-template-text_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/WEB-INF/email/test-template-text_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/WEB-INF/email/test-template-text_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/WEB-INF/email/test-template-text.ftl")).
            andReturn(new File("src/java/test/unit/org/jcatapult/email/test-template-text.ftl").getAbsolutePath());
        EasyMock.expect(containerResolver.getRealPath("/WEB-INF/email/test-template-html_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/WEB-INF/email/test-template-html_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/WEB-INF/email/test-template-html_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/WEB-INF/email/test-template-html_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/WEB-INF/email/test-template-html.ftl")).
            andReturn(new File("src/java/test/unit/org/jcatapult/email/test-template-html.ftl").getAbsolutePath());
        EasyMock.replay(containerResolver);

        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getString("email.templates.location")).andReturn(null);
        EasyMock.expect(config.getBoolean("email.templates.cache", false)).andReturn(true);
        EasyMock.expect(config.getInt("email.templates.check-interval", 2)).andReturn(1);
        EasyMock.expect(config.getStringArray("email.test-template.to")).andReturn(null);
        EasyMock.expect(config.getString("email.test-template.from")).andReturn(null);
        EasyMock.expect(config.getString("email.test-template.subject")).andReturn(null);
        EasyMock.expect(config.getStringArray("email.test-template.cc")).andReturn(null);
        EasyMock.expect(config.getStringArray("email.test-template.bcc")).andReturn(null);
        EasyMock.replay(config);

        MockEmailTransportService transport = new MockEmailTransportService();
        FreeMarkerEmailService service = new FreeMarkerEmailService(transport, config, containerResolver, "/WEB-INF/email");
        service.sendEmail("test-template").andCC(new EmailAddress("from@example.com")).
            andBCC(new EmailAddress("from@example.com")).withSubject("test subject").
            from(new EmailAddress("from@example.com")).to(new EmailAddress("to@example.com")).
            withTemplateParam("key1", "value1").now();
        Assert.assertEquals("test subject", transport.email.getSubject());
        Assert.assertEquals("from@example.com", transport.email.getFrom().getAddress());
        Assert.assertEquals("to@example.com", transport.email.getTo()[0].getAddress());
        Assert.assertEquals("Text value1", transport.email.getText());
        Assert.assertEquals("HTML value1", transport.email.getHtml());
        EasyMock.verify(containerResolver);
        EasyMock.verify(config);
    }

    @Test
    public void testSendConfiguredEmail() throws Exception {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-text_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-text_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-text_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-text_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-text.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-text.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-html_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-html_en_US.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-html_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-html_en.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getRealPath("/org/jcatapult/email/test-template-html.ftl")).andReturn(null);
        EasyMock.expect(containerResolver.getResource("/org/jcatapult/email/test-template-html.ftl")).andReturn(null);
        EasyMock.replay(containerResolver);

        Configuration config = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(config.getString("email.templates.location")).andReturn("/org/jcatapult/email");
        EasyMock.expect(config.getBoolean("email.templates.cache", false)).andReturn(true);
        EasyMock.expect(config.getInt("email.templates.check-interval", 2)).andReturn(1);
        EasyMock.expect(config.getStringArray("email.test-template.to")).andReturn(new String[]{"to@example.com"});
        EasyMock.expect(config.getString("email.test-template.from")).andReturn("from@example.com");
        EasyMock.expect(config.getString("email.test-template.from.display")).andReturn("From Example");
        EasyMock.expect(config.getString("email.test-template.subject")).andReturn("test subject");
        EasyMock.expect(config.getStringArray("email.test-template.cc")).andReturn(new String[]{"from@example.com"});
        EasyMock.expect(config.getStringArray("email.test-template.bcc")).andReturn(new String[]{"from@example.com"});
        EasyMock.replay(config);

        MockEmailTransportService transport = new MockEmailTransportService();
        FreeMarkerEmailService service = new FreeMarkerEmailService(transport, config, containerResolver, "/WEB-INF/email");
        service.sendEmail("test-template").withTemplateParam("key1", "value1").now();
        Assert.assertEquals("test subject", transport.email.getSubject());
        Assert.assertEquals("from@example.com", transport.email.getFrom().getAddress());
        Assert.assertEquals("to@example.com", transport.email.getTo()[0].getAddress());
        Assert.assertEquals("Text value1", transport.email.getText());
        Assert.assertEquals("HTML value1", transport.email.getHtml());
        EasyMock.verify(containerResolver);
        EasyMock.verify(config);
    }

    public static class MockEmailTransportService implements EmailTransportService {
        public Email email;

        public Future<Email> sendEmail(Email email) {
            this.email = email;
            return new Future<Email>() {
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                public boolean isCancelled() {
                    return false;
                }

                public boolean isDone() {
                    return false;
                }

                public Email get() throws InterruptedException, ExecutionException {
                    return null;
                }

                public Email get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return null;
                }
            };
        }
    }
}