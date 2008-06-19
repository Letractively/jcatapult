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
package org.jcatapult.mvc.result.control;

import java.util.Locale;
import java.util.Map;

import org.easymock.EasyMock;
import org.jcatapult.mvc.message.MessageProvider;
import org.jcatapult.container.ContainerResolver;
import org.junit.Ignore;

/**
 * <p>
 * This is an abstract test for helping test input controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class AbstractInputTest extends AbstractControlTest {
    protected MessageProvider makeMessageProvider(String bundleName, String fieldName, Locale locale,
            Map<String, String> attributes, String message) {
        MessageProvider mp = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(mp.getMessage(bundleName, fieldName, locale, attributes)).andReturn(message);
        EasyMock.replay(mp);
        return mp;
    }

    protected ContainerResolver makeContainerResolver(String name) {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/" + name + "_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/" + name + ".ftl");
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/parameter-attributes_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/parameter-attributes.ftl");
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/label_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/label.ftl");
        EasyMock.replay(containerResolver);
        return containerResolver;
    }
}