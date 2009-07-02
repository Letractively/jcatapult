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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcatapult.config.Configuration;
import org.jcatapult.domain.commerce.CreditCard;
import org.jcatapult.domain.commerce.Money;

import com.google.inject.Inject;

/**
 * <p>
 * This implements the billing to use Authorize.net's AIM service. This
 * uses the JCatapult configuration system to determine the AIM URL, user
 * name and password and any other configuration that is needed. The
 * configuration values are:
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Description</th><th>Default</th></tr>
 * <tr><td>jcatapult.commerce.aim.url</td><td>The URL to communicate with</td><td>N/A</td></tr>
 * <tr><td>jcatapult.commerce.aim.username</td><td>The username passed to the AIM server</td><td>N/A</td></tr>
 * <tr><td>jcatapult.commerce.aim.password</td><td>The password passed to the AIM server</td><td>N/A</td></tr>
 * <tr><td>jcatapult.commerce.aim.test</td><td>Determines if this is a test transaction</td><td>false</td></tr>
 * <tr><td>jcatapult.commerce.aim.connectTimeoutSeconds</td><td>The timeout in seconds to wait for the AIM service to respond</td><td>60</td></tr>
 * <tr><td>jcatapult.commerce.aim.readTimeoutSeconds</td><td>The timeout in seconds to wait for the AIM service to respond</td><td>180</td></tr>
 * </table>
 *
 * @author Brian Pontarelli
 */
public class AuthorizeNetCommerceService implements CommerceService {
    private static final Logger logger = Logger.getLogger(AuthorizeNetCommerceService.class.getName());
    private Configuration configuration;

    /**
     * Constructs a new Authorize.net commerce service that uses the given configuration to setup and
     * communicate with the AIM service.
     *
     * @param   configuration The configuration. Read the class comment for the configuration options.
     */
    @Inject
    public AuthorizeNetCommerceService(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommerceResult charge(CreditCard creditCard, Money amount, Money tax, InetAddress userIp)
    throws CommerceException {
        try {
            return sendMessage(creditCard, amount, tax, userIp, TransactionType.CHARGE, null);
        } catch (IOException e) {
            logger.log(Level.FINEST, "IOException", e);
            throw new CommerceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommerceResult authorize(CreditCard creditCard, Money amount, Money tax, InetAddress userIp) throws CommerceException {
        try {
            return sendMessage(creditCard, amount, tax, userIp, TransactionType.AUTHORIZE, null);
        } catch (IOException e) {
            throw new CommerceException(e);
        }
    }

    @Override
    public CommerceResult capture(String transactionID, Money amount, Money tax, InetAddress userIp) throws CommerceException {
        try {
            return sendMessage(null, amount, tax, userIp, TransactionType.CAPTURE, transactionID);
        } catch (IOException e) {
            throw new CommerceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommerceResult verify(CreditCard creditCard, Money amount, Money tax, InetAddress userIp)
    throws CommerceException {
        try {
            return sendMessage(creditCard, amount, tax, userIp, TransactionType.VERIFY, null);
        } catch (IOException e) {
            throw new CommerceException(e);
        }
    }

    private CommerceResult sendMessage(CreditCard creditCard, Money amount, Money tax, InetAddress userIp, TransactionType type, String transactionID)
    throws IOException, CommerceException {
        URL url = new URL(configuration.getString("jcatapult.commerce.aim.url"));
        String username = configuration.getString("jcatapult.commerce.aim.username");
        String password = configuration.getString("jcatapult.commerce.aim.password");
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Contacting AIM with this info");
            logger.fine("URL: " + url.toExternalForm());
            logger.fine("Username: " + username);
            logger.fine("Password: " + password);
        }

        URLConnection huc = url.openConnection();

        StringBuilder build = new StringBuilder();
        build.append("x_version=3.1");
        build.append("&x_delim_data=TRUE");
        build.append("&x_relay_response=FALSE");
        build.append("&x_login=").append(username);
        build.append("&x_tran_key=").append(password);
        build.append("&x_method=CC&");
        build.append("&x_delim_char=|&");

        if (type == TransactionType.AUTHORIZE || type == TransactionType.VERIFY) {
            build.append("&x_type=AUTH_ONLY");
        } else if (type == TransactionType.CAPTURE) {
            build.append("&x_type=PRIOR_AUTH_CAPTURE");
            build.append("&x_trans_id=").append(transactionID);
        } else {
            build.append("&x_type=AUTH_CAPTURE");
        }

        if (configuration.getBoolean("jcatapult.commerce.aim.test")) {
            logger.fine("Authorize.Net commerce service is in *************TEST************* mode");
            build.append("&x_test_request=TRUE");
        } else {
            logger.fine("Authorize.Net commerce service is in *************PRODUCTION************* mode");
        }

        if (tax != null) {
            build.append("&x_tax=").append(tax.toString());
        }

        if (amount != null) {
            build.append("&x_amount=").append(amount.toString());
            build.append("&x_currency_code=").append(amount.getCurrency().getCurrencyCode());
        }

        if (creditCard != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("CC is [" + creditCard + "]");
            }

            if (!creditCard.isVerified()) {
                build.append("&x_card_code=").append(creditCard.getSvn());
                build.append("&x_first_name=").append(creditCard.getFirstName());
                build.append("&x_last_name=").append(creditCard.getLastName());
                build.append("&x_address=").append(creditCard.getAddress().getStreet());
                build.append("&x_city=").append(creditCard.getAddress().getCity());
                build.append("&x_state=").append(creditCard.getAddress().getState());
                build.append("&x_zip=").append(creditCard.getAddress().getPostalCode());
                build.append("&x_country=").append(creditCard.getAddress().getCountry());
                build.append("&x_customer_ip=").append(userIp.toString());
            }
            
            build.append("&x_card_num=").append(creditCard.getNumber());
            build.append("&x_exp_date=").append(creditCard.getExpirationDate());
        }

        huc.setConnectTimeout(configuration.getInt("jcatapult.commerce.aim.connectTimeoutSeconds", 60) * 1000);
        huc.setReadTimeout(configuration.getInt("jcatapult.commerce.aim.readTimeoutSeconds", 180) * 1000);
        huc.setUseCaches(false);
        huc.setDoInput(true);
        huc.setDoOutput(true);
        huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        huc.connect();

        OutputStream os = huc.getOutputStream();
        os.write(build.toString().getBytes());
        os.flush();
        os.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
        String response = br.readLine();
        br.close();

        //logger.finest("Response from AIM is [" + response + "]");

        String[] parts = split(response);
        if (parts.length < 39) {
            throw new CommerceException("Invalid response from the Gateway.");
        }

        int responseCode = Integer.valueOf(parts[0]);
        int reasonCode = Integer.valueOf(parts[2]);
        String reasonText = parts[3];
        String authCode = parts[4];
        String avsCode = parts[5];
        String txnID = parts[6];

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("AIM response is code [" + reasonCode + "]   reason [" + reasonCode + "] [" +
                reasonText + "]    auth [" + authCode + "]     avs [" + avsCode + "]");
        }

        CommerceResult result;
        if (responseCode == 1) {
            result = new CommerceResult(txnID, authCode, avsCode);
        } else if (responseCode == 2) {
            result = new CommerceResult(CommerceError.DECLINED, reasonCode, reasonText, responseCode, avsCode);
        } else {
            result = new CommerceResult(getError(reasonCode), reasonCode, reasonText, responseCode, avsCode);
        }

        return result;
    }

    private String[] split(String response) {
        int index = 0;
        List<String> values = new ArrayList<String>();
        int len = response.length();
        while (index < len) {
            if (response.charAt(index) == '|') {
                values.add("");
                index++;
            } else {
                int next = response.indexOf('|', index);
                if (next >= 0) {
                    values.add(response.substring(index, next));
                    index = next + 1;
                } else {
                    values.add(response.substring(index, len));
                    break;
                }
            }
        }

        return values.toArray(new String[values.size()]);
    }

    private CommerceError getError(int reasonCode) {
        switch (reasonCode) {
            case 5:
                logger.severe("Amount got jacked up or something! FIX THIS!");
                // TODO raise alarm!
                return CommerceError.UNKNOWN_ERROR;
            case 6:
            case 37:
                return CommerceError.INVALID_CREDIT_CARD_NUMBER;
            case 7:
                return CommerceError.INVALID_EXPIRATION;
            case 8:
                return CommerceError.CREDIT_CARD_EXPIRED;
            case 27:
                return CommerceError.INVALID_ADDRESS;
            case 44:
            case 45:
            case 65:
                return CommerceError.INVALID_CVN;
            case 127:
            case 141:
            case 145:
            case 165:
                return CommerceError.DECLINED;
            default:
                return CommerceError.UNKNOWN_ERROR;
        }
    }
}