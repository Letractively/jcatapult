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
package org.jcatapult.mvc.result.message.jsp;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.mvc.result.message.control.Message;

/**
 * <p>
 * This class is a JSP taglib that can retrieve messages from the
 * {@link org.jcatapult.l10n.MessageProvider} and output them.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MessageTag extends TagSupport {
    private String key;
    private String bundle;

    /**
     * @return  The tags key attribute that is the key of the message to fetch.
     */
    public String getKey() {
        return key;
    }

    /**
     * Populates the tags key attribute that is the key of the message to fetch.
     *
     * @param	key The key.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return  The tags bundle attribute that is used to fetch the message is there is not a current
     *          action invocation.
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Populates the tags bundle attribute that is used to fetch the message is there is not a current
     * action invocation.
     *
     * @param	bundle The bundle.
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    @Override
    public int doStartTag() {
        Message message = GuiceContainer.getInjector().getInstance(Message.class);
        message.render(pageContext.getOut(), key, bundle);
        return Tag.EVAL_PAGE;
    }
}