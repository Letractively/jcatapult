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
package org.jcatapult.domain.commerce;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the hibernate types.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class MoneyAmountUSDTypeTest extends JPABaseTest {
    protected PersistenceService persistenceService;

    @Inject
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Test
    public void testSave() {
        MoneyAmountHolder holder = new MoneyAmountHolder();
        holder.setMoney(Money.valueOf("1.99", Currency.getInstance("USD")));
        persistenceService.persist(holder);
    }

    @Test
    public void testCurrency() {
        List<MoneyAmountHolder> holders = persistenceService.findAllByType(MoneyAmountHolder.class);
        assertEquals(1, holders.size());
        assertEquals(new BigDecimal("1.99"), holders.get(0).getMoney().toBigDecimal());
        assertEquals(Currency.getInstance("USD"), holders.get(0).getMoney().getCurrency());
    }

    @Test
    public void testBadCurrency() {
        MoneyAmountHolder holder = new MoneyAmountHolder();
        holder.setMoney(Money.valueOf("1.99", Currency.getInstance("EUR")));
        try {
            persistenceService.persist(holder);
            fail("Should have failed");
        } catch (Exception e) {
            // Expected
        }
    }
}