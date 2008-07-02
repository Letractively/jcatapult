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
package org.jcatapult.mvc.result.message.control;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.jcatapult.l10n.MessageProvider;
import org.jcatapult.mvc.action.ActionInvocationStore;

import com.google.inject.Inject;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * <p>
 * This class a a FreeMarker method model as well as the control for
 * the message JSP tag.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Message implements TemplateMethodModel, TemplateDirectiveModel {
    private final MessageProvider messageProvider;
    private final Object action;

    @Inject
    public Message(MessageProvider messageProvider, ActionInvocationStore actionInvocationStore) {
        this.messageProvider = messageProvider;
        this.action = actionInvocationStore.get().action();
    }

    /**
     * <p>
     * Grabs the message from the provider and outputs it to the given writer.
     * </p>
     *
     * @param   writer The writer to output to.
     * @param   key The key of the message.
     * @param   bundle (optional) The bundle to use if the action is null.
     */
    public void render(Writer writer, String key, String bundle) {
        if (bundle == null) {
            if (action == null) {
                throw new IllegalStateException("The current URI doesn't have an action class " +
                    "associated with it and the bundle attribute is null.");
            }

            bundle = action.getClass().getName();
        }

        String message = messageProvider.getMessage(bundle, key);
        try {
            writer.write(message);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write to Writer", e);
        }
    }

    /**
     * Calls the {@link #render(Writer, String, String)} method using a StringWriter to collect
     * the result and the first and second parameters to the method. The first is the key and the
     * second is the bundle, which can be left out.
     *
     * @param   arguments The method arguments.
     * @return  The result.
     * @throws  TemplateModelException If the action is null and bundle is not specified.
     */
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1 || arguments.size() != 2) {
            throw new TemplateModelException("Invalid parameters to the message method. This method " +
                "takes one or two parameters like this: message(key) or message(key, bundle)");
        }

        StringWriter writer = new StringWriter();
        String key = (String) arguments.get(0);
        String bundle = (String) (arguments.size() > 1 ? arguments.get(1) : null);
        render(writer, key, bundle);
        return writer.toString();
    }

    /**
     * Calls the {@link #render(Writer, String, String)} method using the Writer from the Environment
     * and the attributes passed to the directive. The attributes must be {@code key} and {@code bundle}
     * but the bundle attribute is optional.
     *
     * @param   env The Environment to get the Writer from.
     * @param   params The attributes passed to the directive.
     * @param   loopVars Not used.
     * @param   body Not used.
     * @throws  TemplateException If the action is null and bundle is not specified.
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
    throws TemplateException, IOException {
        String key = params.get("key").toString();
        String bundle = params.get("bundle").toString();
        render(env.getOut(), key, bundle);
    }
}