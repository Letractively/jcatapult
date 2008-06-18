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
package org.jcatapult.mvc.message;

import java.util.List;
import java.util.Map;

import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.message.scope.MessageType;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the mechanism by which errors are added and fetched.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultMessageStore.class)
public interface MessageStore {
    /**
     * Usually called by the {@link org.jcatapult.mvc.parameter.ParameterWorkflow}, this method adds
     * a conversion error for the given parameter. The values can be token replaced within the localized
     * error message.
     *
     * @param   field The name of the field that the conversion error failed for.
     * @param   bundle The name of the bundle that the error is pulled from.
     * @param   attributes The parameter attributes, which might be useful for error messaging stuff
     * @param   values The values attempting to be set into the field.
     * @throws  MissingMessageException If the conversion message is missing.
     */
    void addConversionError(String field, String bundle, Map<String, String> attributes, Object... values) throws MissingMessageException;

    /**
     * Adds a field message which is pulled from the {@link MessageProvider}.
     *
     * @param   scope The scope that the message should be placed into.
     * @param   field The field that the message is associated with.
     * @param   bundle The bundle the message should be pulled from.
     * @param   key The key in the bundle.
     * @param   values The values used to format the message.
     * @throws  MissingMessageException If the message is missing.
     */
    void addFieldMessage(MessageScope scope, String field, String bundle, String key, Object... values) throws MissingMessageException;

    /**
     * Adds a field message which is pulled from the {@link MessageProvider}. The name of the bundle
     * is the same as the class name of the current action that the request is associated with. If
     * there is no action, this method will throw an exception.
     *
     * @param   scope The scope that the message should be placed into.
     * @param   field The field that the message is associated with.
     * @param   key The key in the bundle.
     * @param   values The values used to format the message.
     * @throws  MissingMessageException If the message is missing.
     * @throws  IllegalStateException If there is no action class associated with the current request.
     */
    void addFieldMessage(MessageScope scope, String field, String key, Object... values)
    throws MissingMessageException, IllegalStateException;

    /**
     * Adds a field error which is pulled from the {@link MessageProvider}.
     *
     * @param   scope The scope that the error should be placed into.
     * @param   field The field that the error is associated with.
     * @param   bundle The bundle the error should be pulled from.
     * @param   key The key in the bundle.
     * @param   values The values used to format the error.
     * @throws  MissingMessageException If the error is missing.
     */
    void addFieldError(MessageScope scope, String field, String bundle, String key, Object... values)
    throws MissingMessageException;

    /**
     * Adds a field error which is pulled from the {@link MessageProvider}. The name of the bundle
     * is the same as the class name of the current action that the request is associated with. If
     * there is no action, this method will throw an exception.
     *
     * @param   scope The scope that the error should be placed into.
     * @param   field The field that the error is associated with.
     * @param   key The key in the bundle.
     * @param   values The values used to format the error.
     * @throws  MissingMessageException If the error is missing.
     * @throws  IllegalStateException If there is no action class associated with the current request.
     */
    void addFieldError(MessageScope scope, String field, String key, Object... values) throws MissingMessageException;

    /**
     * Adds an action message which is pulled from the {@link MessageProvider}.
     *
     * @param   scope The scope that the message should be placed into.
     * @param   bundle The bundle the message should be pulled from.
     * @param   key The key in the bundle.
     * @param   values The values used to format the message.
     * @throws  MissingMessageException If the message is missing.
     */
    void addActionMessage(MessageScope scope, String bundle, String key, Object... values) throws MissingMessageException;

    /**
     * Adds an action message which is pulled from the {@link MessageProvider}. The name of the bundle
     * is the same as the class name of the current action that the request is associated with. If
     * there is no action, this method will throw an exception.
     *
     * @param   scope The scope that the message should be placed into.
     * @param   key The key in the bundle.
     * @param   values The values used to format the message.
     * @throws  MissingMessageException If the message is missing.
     * @throws  IllegalStateException If there is no action class associated with the current request.
     */
    void addActionMessage(MessageScope scope, String key, Object... values) throws MissingMessageException;

    /**
     * Adds an action error which is pulled from the {@link MessageProvider}.
     *
     * @param   scope The scope that the error should be placed into.
     * @param   bundle The bundle the error should be pulled from.
     * @param   key The key in the bundle.
     * @param   values The values used to format the error.
     * @throws  MissingMessageException If the error is missing.
     */
    void addActionError(MessageScope scope, String bundle, String key, Object... values) throws MissingMessageException;

    /**
     * Adds an action errorwhich is pulled from the {@link MessageProvider}. The name of the bundle
     * is the same as the class name of the current action that the request is associated with. If
     * there is no action, this method will throw an exception.
     *
     * @param   scope The scope that the error should be placed into.
     * @param   key The key in the bundle.
     * @param   values The values used to format the error.
     * @throws  MissingMessageException If the error is missing.
     * @throws  IllegalStateException If there is no action class associated with the current request.
     */
    void addActionError(MessageScope scope, String key, Object... values) throws MissingMessageException;

    /**
     * Fetches all of the action messages that are currently available. Not mutable.
     *
     * @param   type The message type to fetch (error or plain messages).
     * @return  The List of action messages and never null.
     */
    List<String> getActionMessages(MessageType type);

    /**
     * Fetches all of the field messages that are currently available. Not mutable.
     *
     * @param   type The message type to fetch (error or plain messages).
     * @return  The Map of field messages and never null.
     */
    Map<String, List<String>> getFieldMessages(MessageType type);

    /**
     * Clears all of the action messages in all scopes of the given type.
     *
     * @param   type The type of messages to clear (error or plain messages).
     */
    void clearActionMessages(MessageType type);

    /**
     * Clears all of the field messages in all scopes of the given type.
     *
     * @param   type The type of messages to clear (error or plain messages).
     */
    void clearFieldMessages(MessageType type);
}