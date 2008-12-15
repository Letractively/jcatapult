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
 * This class stores the results of a commerce request.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ChargeResult {
    private final String transactionID;
    private final CommerceError error;
    private final String gatewayResponseCode;
    private final String gatewayApprovalCode;
    private final String gatewayErrorCode;
    private final String gatewayErrorMessage;
    private final String gatewayAVSCode;

    public ChargeResult(String transactionID, CommerceError error, String gatewayResponseCode,
            String gatewayApprovalCode, String gatewayErrorCode, String gatewayErrorMessage,
            String gatewayAVSCode) {
        this.transactionID = transactionID;
        this.error = error;
        this.gatewayResponseCode = gatewayResponseCode;
        this.gatewayApprovalCode = gatewayApprovalCode;
        this.gatewayErrorCode = gatewayErrorCode;
        this.gatewayErrorMessage = gatewayErrorMessage;
        this.gatewayAVSCode = gatewayAVSCode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public CommerceError getError() {
        return error;
    }

    public String getGatewayResponseCode() {
        return gatewayResponseCode;
    }

    public String getGatewayApprovalCode() {
        return gatewayApprovalCode;
    }

    public String getGatewayErrorCode() {
        return gatewayErrorCode;
    }

    public String getGatewayErrorMessage() {
        return gatewayErrorMessage;
    }

    public String getGatewayAVSCode() {
        return gatewayAVSCode;
    }
}