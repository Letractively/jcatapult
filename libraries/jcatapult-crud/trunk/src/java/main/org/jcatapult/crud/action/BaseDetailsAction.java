/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.crud.action;

import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.persistence.domain.Identifiable;

import com.google.inject.Inject;

/**
 * <p>
 * This is a generic base action for showing details of single row in the search
 * results. This class is helpful because it handles next and prev buttons on
 * the details page.
 * </p>
 *
 * <p>
 * An implementation might look like this:
 * </p>
 *
 * <pre>
 * @@Action
 * public class Details extends BaseDetailsAction&lt;User> {
 *   // Needed to calculate next and prev buttons
 *   @@ActionSession(action = Index.class)
 *   public List&lt;T> results;
 *
 *   // Needed to calculate next and prev buttons
 *   @@ActionSession
 *   public SearchCriteria&lt;T> searchCriteria;
 *
 *   protected Class&lt;User> getType() {
 *     return User.class;
 *   }
 *
 *   protected List&lt;User> getResults() {
 *     return results;
 *   }
 *
 *   protected SearchCriteria&lt;User> getSearchCriteria() {
 *     return searchCriteria;
 *   }
 * }
 * </pre>
 *
 * @author  Brian Pontarelli
 */
public abstract class BaseDetailsAction<T extends Identifiable> extends BaseCrudAction<T> {
    protected MessageStore messageStore;

    /**
     * The ID of the entity to display the details for. (Input)
     */
    public int id;

    /**
     * The entity.  (Output)
     */
    public T result;

    @Inject
    public void setMessageStore(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    /**
     * Fetches the entity from the database using the ID and {@link #getType()} return value. If the
     * entity is missing, this puts an action error into the message store using the key
     * <code>missing</code>.
     *
     * @return  The String <code>missing</code> if the entity doesn't exist, <code>success</code>
     *          otherwise. 
     */
    public String execute() {
        result = searchService.findById(getType(), id);
        if (result == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing");
            return "missing";
        }

        // Setup the ids
        setupNextPrevIds();

        return "success";
    }

    /**
     * Sub-classes must implement this to provide the result type.
     *
     * @return  The result type.
     */
    protected abstract Class<T> getType();
}