/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.service;

import org.jcatapult.commerce.service.CommerceError;

/**
 * <p>
 * This class is the result of attempting to update a credit
 * card.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CreditCardUpdateResult {
    private final CommerceError commerceError;
    private final UpdateResult updateResult;

    public CreditCardUpdateResult(CommerceError commerceError, UpdateResult updateResult) {
        this.commerceError = commerceError;
        this.updateResult = updateResult;
    }

    public CommerceError getCommerceError() {
        return commerceError;
    }

    public UpdateResult getUpdateResult() {
        return updateResult;
    }

    public boolean isError() {
        return commerceError != null || updateResult == UpdateResult.ERROR || updateResult == UpdateResult.MISSING;
    }
}