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
package org.jcatapult.persistence.domain;

/**
 * <p> This interface marks a class as never being deleted permanently from the database. Instead, it is marked as being
 * deleted and can be resurrected. </p>
 *
 * @author Brian Pontarelli
 */
public interface SoftDeletable {
  /**
   * @return True if this entity has been deleted, false otherwise.
   */
  boolean isDeleted();

  /**
   * Sets whether or not this entity has been deleted.
   *
   * @param deleted True if the entity is deleted, false otherwise.
   */
  void setDeleted(boolean deleted);
}