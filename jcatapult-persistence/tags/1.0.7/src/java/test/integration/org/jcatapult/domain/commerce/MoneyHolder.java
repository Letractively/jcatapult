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

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p> This is a test object for money </p>
 *
 * @author Brian Pontarelli
 */
@Entity
public class MoneyHolder extends IdentifiableImpl {
  @Type(type = "org.jcatapult.domain.commerce.MoneyCurrencyType")
  @Columns(columns = {
    @Column(name = "amount"),
    @Column(name = "currency")
  })
  private Money money;

  public Money getMoney() {
    return money;
  }

  public void setMoney(Money money) {
    this.money = money;
  }
}