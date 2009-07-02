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

/**
 * <p>
 * This class stores the results of a commerce request.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class CommerceResult {
    private final String transactionID;
    private final CommerceError error;
    private final int gatewayResponseCode;
    private final int gatewayErrorCode;
    private final String gatewayErrorMessage;
    private final String gatewayApprovalCode;
    private final String gatewayAVSCode;

    public CommerceResult(String transactionID, String gatewayApprovalCode, String gatewayAVSCode) {
        this.transactionID = transactionID;
        this.gatewayApprovalCode = gatewayApprovalCode;
        this.gatewayAVSCode = gatewayAVSCode;
        this.gatewayResponseCode = 1;
        this.gatewayErrorCode = -1;
        this.gatewayErrorMessage = null;
        this.error = null;
    }

    public CommerceResult(CommerceError error, int gatewayErrorCode, String gatewayErrorMessage,
            int gatewayResponseCode, String gatewayAVSCode) {
        this.gatewayResponseCode = gatewayResponseCode;
        this.gatewayAVSCode = gatewayAVSCode;
        this.gatewayErrorMessage = gatewayErrorMessage;
        this.gatewayErrorCode = gatewayErrorCode;
        this.error = error;
        this.gatewayApprovalCode = null;
        this.transactionID = null;
    }

    /**
     * @return  The transaction ID, which is generally from the payment gateway.
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * @return  The error enumeration, which is normally decoded by the implementations from the
     *          gateway. This is the normalized result that code can always rely on it being
     *          consistent between gateway implementations.
     */
    public CommerceError getError() {
        return error;
    }

    /**
     * @return  Gateway specific response code.
     */
    public int getGatewayResponseCode() {
        return gatewayResponseCode;
    }

    /**
     * @return  Gateway specific approval code or null if the transaction failed.
     */
    public String getGatewayApprovalCode() {
        return gatewayApprovalCode;
    }

    /**
     * @return  Gateway specific error code or null if the transaction was successful.
     */
    public int getGatewayErrorCode() {
        return gatewayErrorCode;
    }

    /**
     * @return  Gateway specific error message or null if the transaction was successful.
     */
    public String getGatewayErrorMessage() {
        return gatewayErrorMessage;
    }

    /**
     * @return  Gateway specific AVS code.
     */
    public String getGatewayAVSCode() {
        return gatewayAVSCode;
    }

    /**
     * @return  True if this result is an error, false otherwise.
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * Produces a debug String that contains all of the information from this result. Anything that
     * is null will have the String <code>null</code> inside the return value.
     *
     * @return  The debug String.
     */
    public String toString() {
        return "Credit card charge result: Response code [" + gatewayResponseCode +
            "]. Success info [approval code {" + gatewayApprovalCode + "}, transaction ID {" +
            transactionID + "}]. Error info [error {" + error + "}, gateway error code {" +
            gatewayErrorCode + "}, gateway error message{" + gatewayErrorMessage + "}]. AVS code [" +
            gatewayAVSCode + "]";
    }
}