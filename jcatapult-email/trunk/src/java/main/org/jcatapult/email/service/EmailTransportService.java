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

import java.util.concurrent.Future;

import org.jcatapult.email.domain.Email;
import org.jcatapult.email.service.JavaMailEmailTransportService;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the transport mechanism for sending email
 * messages.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(JavaMailEmailTransportService.class)
public interface EmailTransportService {
    /**
     * Sends an email using some SMTP transport mechanism. An implementation might decide to make the
     * sending be asynchronous or it might choose to send the email immediately. If it decides to send
     * the email immediately, this method will return null.
     *
     * @param   email The email to send.
     * @return  A Future for implementations that send email asynchronously.
     */
    Future<Email> sendEmail(Email email);
}