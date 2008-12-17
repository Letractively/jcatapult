/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action.account.creditCard;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Inject;

import org.jcatapult.module.user.domain.AuditableCreditCard;

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