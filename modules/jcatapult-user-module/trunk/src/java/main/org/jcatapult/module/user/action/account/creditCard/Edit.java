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

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.module.user.domain.AuditableCreditCard;
import org.jcatapult.module.user.service.CreditCardUpdateResult;
import org.jcatapult.module.user.service.UserService;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.validation.annotation.ValidateMethod;
import org.jcatapult.security.SecurityContext;
import org.joda.time.YearMonthDay;

import com.google.inject.Inject;
import net.java.lang.StringTools;

/**
 * <p>
 * This action generates the edit credit card form that can be used by
 * application users to edit their credit cards. This action is
 * secure from hacking because it fetches the user's account based
 * on the username in the {@link SecurityContext} rather than an
 * HTTP parameter.
 * </p>
 *
 * <h3>Localization keys</h3>
 * <p>
 * These keys can be used to override the default error message
 * inside the application.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "/account/summary")
public class Edit {
    private final MessageStore messageStore;
    private final UserService userService;
    private final HttpServletRequest request;
    public AuditableCreditCard creditCard;
    public Integer id;

    @Inject
    public Edit(MessageStore messageStore, UserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
        this.messageStore = messageStore;
    }

    /**
     * Handles form render by fetching the credit card.
     *
     * @return  Always input.
     */
    public String get() {
        String username = SecurityContext.getCurrentUsername();
        creditCard = userService.findCreditCard(id, username);
        return "input";
    }

    /**
     * Handles form submission and saves the credit card.
     *
     * @return  The result, {@code error} or {@code success}. {@code error} is only returned if the
     *          update failed according to the method
     *          {@link UserService#updateCreditCard(AuditableCreditCard, String, InetAddress)}.
     */
    public String post() {
        String username = SecurityContext.getCurrentUsername();
        InetAddress ip;
        try {
            ip = InetAddress.getByName(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            try {
                ip = InetAddress.getLocalHost();
            } catch (UnknownHostException e1) {
                // Not really possible unless this isn't a server with an ethernet card.
                throw new RuntimeException(e1);
            }
        }

        CreditCardUpdateResult result = userService.updateCreditCard(creditCard, username, ip);
        if (result.isError()) {
            if (result.getCommerceError() != null) {
                messageStore.addActionError(MessageScope.REQUEST, "commerce.error." + result.getCommerceError().toString().toLowerCase());
            } else {
                messageStore.addActionError(MessageScope.REQUEST, "error");
            }

            return "error";
        }

        return "success";
    }

    /**
     * Validates new credit cards, ensures existing cards exist, validates license is accepted.
     */
    @ValidateMethod
    public void validate() {
        if (StringTools.isTrimmedEmpty(creditCard.getNumber())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.number", "creditCard.number.required");
        }
        if (creditCard.getExpirationDate().isBefore(new YearMonthDay())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.expirationMonth", "creditCard.expirationMonth.invalid");
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.expirationYear", "creditCard.expirationYear.invalid");
        }
        if (StringTools.isTrimmedEmpty(creditCard.getSvn())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.svn", "creditCard.svn.required");
        }
        if (StringTools.isTrimmedEmpty(creditCard.getLastName())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.lastName", "creditCard.lastName.required");
        }
        if (StringTools.isTrimmedEmpty(creditCard.getFirstName())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.firstName", "creditCard.firstName.required");
        }
        if (StringTools.isTrimmedEmpty(creditCard.getAddress().getStreet())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.address.street", "creditCard.address.street.required");
        }
        if (StringTools.isTrimmedEmpty(creditCard.getAddress().getCity())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.address.city", "creditCard.address.city.required");
        }
        if (StringTools.isTrimmedEmpty(creditCard.getAddress().getCountry())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.address.country", "creditCard.address.country.required");
        } else if (creditCard.getAddress().getCountry().equals("US") &&
                StringTools.isTrimmedEmpty(creditCard.getAddress().getState())) {
            messageStore.addFieldError(MessageScope.REQUEST, "creditCard.address.state", "creditCard.address.state.required");
        }

    }
}