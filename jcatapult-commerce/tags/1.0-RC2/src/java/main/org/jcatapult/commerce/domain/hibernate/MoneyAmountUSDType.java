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
package org.jcatapult.commerce.domain.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Currency;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.jcatapult.commerce.domain.Money;

/**
 * <p>
 * This class is a hibernate type for storing currencies in the database using
 * only a single column for the amount. This always assumes USD for the currency.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MoneyAmountUSDType implements UserType {
    private static final int[] SQL_TYPES = new int[] {Types.DOUBLE};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return Money.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }

        Money moneyx = (Money) x;
        Money moneyy  = (Money) y;

        return moneyx.equalsRounded(moneyy);
    }

    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    public Object nullSafeGet(ResultSet resultSet, String[] strings, Object object)
    throws HibernateException, SQLException {
        Object value = Hibernate.BIG_DECIMAL.nullSafeGet(resultSet, strings[0]);
        if (value == null) {
            return null;
        }

        return Money.valueOf((BigDecimal) value, Currency.getInstance("USD"));
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
    throws HibernateException, SQLException {
        if (value == null) {
            Hibernate.BIG_DECIMAL.nullSafeSet(preparedStatement, null, index);
        } else {
            Money money = (Money) value;
            if (!money.getCurrency().getCurrencyCode().equals("USD")) {
                throw new HibernateException("The type you specified as the MoneyAmountUSDType but " +
                    "you passed a money to an object that had a currency code other than USD. The " +
                    "currency code you supplied was [" + money.getCurrency().getCurrencyCode() + "]");
            }

            Hibernate.BIG_DECIMAL.nullSafeSet(preparedStatement, money.toBigDecimal(), index);
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }

        Money money = (Money) value;
        if (!money.getCurrency().getCurrencyCode().equals("USD")) {
            throw new HibernateException("The type you specified as the MoneyAmountUSDType but " +
                "you passed a money to an object that had a currency code other than USD. The " +
                "currency code you supplied was [" + money.getCurrency().getCurrencyCode() + "]");
        }

        return Money.valueOf(money.toBigDecimal(), money.getCurrency());
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object value) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}