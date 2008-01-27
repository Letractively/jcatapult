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

import org.junit.Assert;
import org.junit.Test;
import org.jcatapult.email.domain.Email;

/**
 * <p>
 * This class tests the email command.
 * </p>
 *
 * @author  James Humphrey
 */
public class EmailBuilderImplTest {
    @Test
    public void testTemplateParams() {
        EmailCommand eb = new EmailCommandImpl(null, null, new Email());
        eb = eb.withTemplateParam("key1", "value1").withTemplateParam("key2", "value2");

        Assert.assertEquals(2, eb.getTemplateParams().size());

        Assert.assertEquals("value1", eb.getTemplateParams().get("key1"));
        Assert.assertEquals("value2", eb.getTemplateParams().get("key2"));
    }

    @Test
    public void testSubjectExplicit() {
        EmailCommand eb = new EmailCommandImpl(null, null, new Email());
        eb.subject("test subject");
        Assert.assertEquals("test subject", eb.getSubject());
    }
}
