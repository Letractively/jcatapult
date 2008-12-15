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
package org.jcatapult.commerce;

/**
 * <p>
 * This enumeration is a billing error.
 * </p>
 *
 * @author Brian Pontarelli
 */
public enum CommerceError {
    INVALID_CREDIT_CARD_NUMBER,
    INVALID_CREDIT_CARD,
    INVALID_CVN,
    INVALID_ADDRESS,
    INVALID_EXPIRATION,
    INVALID_NAME,
    COMMUNICATION_ERROR,
    UNKNOWN_ERROR,
    CREDIT_CARD_EXPIRED,

    /**
     * Indicates that the card is maxed out or has been suspected of fraud or closed or the person
     * has committed a triple murder and is wanted by the FBI and their bank accounts have been
     * frozen.
     */
    DECLINED;
}
