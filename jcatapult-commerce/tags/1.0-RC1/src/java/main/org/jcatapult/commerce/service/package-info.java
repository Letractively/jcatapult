/**
 * <p>
 * This package contains the JCatapult commerce services and the associated
 * classes.
 * </p>
 * <p>
 * Currently, JCatapult doesn't provide support for complete eCommerce systems.
 * Instead, it simply provides the ability to process credit card transactions
 * in a unified manner. This package contains service interfaces and result
 * classes that allow applications to charge and verify credit cards.
 * </p>
 * <p>
 * Currently, there is only a single implementation for the Authorize.net
 * payment gateway. In addition, these services are mainly designed for
 * direct connect gateways that will receive submissions and then return
 * the results in a single transaction. Payment processing such as Google
 * Checkout and PayPal Basic are not supported by JCatapult because they
 * require two phase transactions where the gateway connects back to the
 * application to return the result and sometimes require that the user
 * be taken to the PayPal or Google website to complete the transaction.
 * </p>
 * <p>
 * In the future we plan to support those forms of payment, but for now,
 * you'll need a direct connect account with a gateway such as Authorize.net
 * or PaymentTech in order to use these interfaces.
 * </p>
 */
package org.jcatapult.commerce.service;