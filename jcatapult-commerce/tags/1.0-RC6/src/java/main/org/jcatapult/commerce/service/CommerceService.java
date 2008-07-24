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
package org.jcatapult.commerce.service;

import java.net.InetAddress;

import org.jcatapult.domain.commerce.CreditCard;
import org.jcatapult.domain.commerce.Money;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the most common operations for commerce
 * via an online merchant. This interface doesn't define how the
 * application will handle the result of successful or unsuccessful
 * commerce submission, it only defines how the payments are submitted
 * to commerce gateways.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(AuthorizeNetCommerceService.class)
public interface CommerceService {
    /**
     * Charges the given credit and the given amount.
     *
     * @param   creditCard The credit card.
     * @param   amount The amount to charge the credit card. This should include the tax.
     * @param   tax The amount of the transaction that is tax.
     * @param   userIp The IP address of the client computer that is submitting the commerce request.
     *          This is used for fraud tracking.
     * @return  The transaction result from the commerce gateway.
     * @throws  CommerceException If the charge failed.
     */
    ChargeResult chargeCard(CreditCard creditCard, Money amount, Money tax, InetAddress userIp)
    throws CommerceException;

    /**
     * Verifies that the credit card is valid by pre-authing the amount given. Some gateways have
     * other means of verification, but this is the most common. This pre-auth will revert after an
     * amount of time specified by the gateway or the credit cards bank.
     *
     * @param   creditCard The credit card.
     * @param   amount The amount to pre-auth the credit card. This should include the tax.
     * @param   tax The amount of the transaction that is tax.
     * @param   userIp The IP address of the client computer that is submitting the commerce request.
     *          This is used for fraud tracking.
     * @return  The verify result from the commerce gateway.
     * @throws  CommerceException If the verify failed.
     */
    VerifyResult verifyCard(CreditCard creditCard, Money amount, Money tax, InetAddress userIp)
    throws CommerceException;
}