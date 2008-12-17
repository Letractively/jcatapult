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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

/**
 * <p>
 * This class represents an internationalize monetary value.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Money implements Serializable, Comparable<Money> {
    private final static int serialVersionUID = 1;

    private final BigDecimal amount;
    private final Currency currency;

    private Money(String amount, Currency currency) {
        this.amount = new BigDecimal(amount);
        this.currency = currency;
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money valueOf(String amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money valueOf(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money plus(Money that) {
        return new Money(amount.add(that.amount), currency);
    }

    public Money minus(Money that) {
        return new Money(amount.subtract(that.amount), currency);
    }

    public Money times(long l) {
        return new Money(amount.multiply(new BigDecimal(l)), currency);
    }

    public Money times(Double d) {
        return new Money(amount.multiply(new BigDecimal(d)), currency);
    }

    public Money dividedBy(long l) {
        return new Money(amount.divide(new BigDecimal(l)), currency);
    }

    public Money dividedBy(double d) {
        return new Money(amount.divide(new BigDecimal(d)), currency);
    }

    public long longValue() {
        return amount.longValue();
    }

    public double doubleValue() {
        return amount.doubleValue();
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    /**
     * @return  A String representation of the money that has been rounded to the currencies fractional
     *          digits.
     */
    public String toNumericString() {
        BigDecimal bd = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        return bd.toPlainString();
    }

    /**
     * @return  A String of the money with the currency code for the current locale.
     */
    public String toString() {
        return currency.getSymbol() + toNumericString();
    }

    /**
     * @param   locale The locale to use for displaying the currency code.
     * @return  A String of the money with the currency code for the givan locale.
     */
    public String toString(Locale locale) {
        return currency.getSymbol(locale) + toNumericString();
    }

    /**
     * This method compares the rounded values, which nearly always be equivalent depending on how that
     * value was acquired. For example, 1.00/3 == 0.33. This is computed by converting the two Monies
     * to rounded Strings and comparing the Strings.
     *
     * @param   money The Money to compare.
     * @return  True if they are rounded and then equal, false if not.
     */
    public boolean equalsRounded(Money money) {
        if (this == money) return true;
        String str = toNumericString();
        String other = money.toNumericString();
        if (!str.equals(other)) return false;
        if (!currency.equals(money.currency)) return false;

        return true;
    }

    /**
     * This method compares the exact values, which often will not be equivalent depending on how that
     * value was acquired. For example, 1.00/3 != 0.33.
     *
     * @param   money The Money to compare.
     * @return  True if they are exactly equal, false if not.
     */
    public boolean equalsExact(Money money) {
        if (this == money) return true;
        if (!amount.equals(money.amount)) return false;
        if (!currency.equals(money.currency)) return false;

        return true;
    }

    /**
     * This throws an exception currently because of rounding issues.
     *
     * @param   obj Not used.
     * @return  Nothing.
     */
    public boolean equals(Object obj) {
        throw new AssertionError("Equals is not implemented on Money because of really hairy issues " +
            "with rounding and maintaining transitive/associative properties of this object.");
    }

    public int compareTo(Money o) {
        if (!currency.equals(o.currency)) {
            throw new IllegalArgumentException("Unable to compare monies with different currencies");
        }

        return amount.compareTo(o.amount);
    }

    public int hashCode() {
        int result;
        result = amount.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
}