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
 *
 */
package org.jcatapult.module.user.action.account.creditCard;

import org.jcatapult.module.user.domain.AuditableCreditCard;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Inject;

/**
 * <p>
 * This class deletes the credit card.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
public class ConfirmDelete {
    private final MessageStore messageStore;
    private final PersistenceService persistenceService;
    public AuditableCreditCard creditCard;
    public Integer id;

    @Inject
    public ConfirmDelete(PersistenceService persistenceService, MessageStore messageStore) {
        this.persistenceService = persistenceService;
        this.messageStore = messageStore;
    }

    public String execute() {
        String username = SecurityContext.getCurrentUsername();
        creditCard = persistenceService.queryFirst(AuditableCreditCard.class,
            "select acc from AuditableCreditCard acc where acc.id = ?1 and acc.user.login = ?2", id, username);
        if (creditCard == null) {
            messageStore.addActionMessage(MessageScope.REQUEST, "missing.credit.card");
            return "error";
        }

        return "success";
    }
}