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

import java.util.concurrent.Future;

import net.java.util.NameValuePairChain;
import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines how to send emails in a simple and
 * templatized manner.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(FreeMarkerEmailService.class)
public interface EmailService {
    /**
     * Sends an email to the given email address from the given email address. This method loads a
     * template using the template name given and passes into the template the list of parameters
     * given. Because most template engines supports named parameters and I hate creating Map
     * objects (too heavy weight in Java), I created a class in Java.net Commons to support name
     * chaining. So, if you have a template like this:
     *
     * <pre>
     * Hello ${name},
     *
     * Thanks for buying all that cool stuff.
     * </pre>
     *
     * <p>
     * You would call this method like this:
     * </p>
     *
     * <pre>
     * sendEmail("fred@example.com", "mary@example1.com", "someTemplate").nvp("name", "Fred").done();
     * </pre>
     *
     * @param   template (Required) The name of the template. The implementation will dictate the type
     *          of template and how they are stored.
     * @param   subject (Optional) The email subject.
     * @param   from (Optional) The from email address.
     * @param   to (Optional) The to email addresses.
     * @return  The name value pair chain.
     */
    NameValuePairChain<String, Object, Future<Email>> sendEmail(String template, String subject,
            String from, String... to);

    /**
     * Sends an email to the given email address (unless the varargs to parameters are not given).
     * The subject, from and other email details need to be supplied via some other mechanism such
     * as a configuration object. Other than that, this method chains to the
     * {@link #sendEmail(String, String, String, String[])} method passing in null and the to vararg
     * list.
     *
     * @param   template (Required) The name of the template. The implementation will dictate the type
     *          of template and how they are stored.
     * @param   to (Optional) The to email addresses.
     * @return  The name value pair chain.
     */
    NameValuePairChain<String, Object, Future<Email>> sendConfiguredEmail(String template, String... to);
}