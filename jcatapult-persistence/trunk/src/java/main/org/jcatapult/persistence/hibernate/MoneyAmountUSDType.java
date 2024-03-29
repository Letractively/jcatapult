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
package org.jcatapult.persistence.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BigDecimalType;
import org.hibernate.usertype.UserType;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

/**
 * This class is a hibernate type for storing currencies in the database using only a single column for the amount.
 * This always assumes USD for the currency.
 *
 * @author Brian Pontarelli
 */
public class MoneyAmountUSDType implements UserType {
  private static final int[] SQL_TYPES = new int[]{Types.DOUBLE};

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
    Money moneyy = (Money) y;

    return moneyx.equals(moneyy);
  }

  public int hashCode(Object object) throws HibernateException {
    return object.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    Object value = BigDecimalType.INSTANCE.nullSafeGet(rs, names, session, owner);
    if (value == null) {
      return null;
    }

    return Money.of(CurrencyUnit.USD, (BigDecimal) value);
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    if (value == null) {
      BigDecimalType.INSTANCE.nullSafeSet(st, value, index, session);
    } else {
      Money money = (Money) value;
      if (!money.getCurrencyUnit().getCurrencyCode().equals("USD")) {
        throw new HibernateException("The type you specified as the MoneyAmountUSDType but " +
          "you passed a money to an object that had a currency code other than USD. The " +
          "currency code you supplied was [" + money.getCurrencyUnit().getCurrencyCode() + "]");
      }

      BigDecimalType.INSTANCE.nullSafeSet(st, money.getAmount(), index, session);
    }
  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
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