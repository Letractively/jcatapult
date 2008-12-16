/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.service;

import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface provides a mechanism for getting flags which control the
 * functionality of the User module. The default implementation for example
 * controls which fields are required for the user.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultUserConfiguration.class)
public interface UserConfiguration {
    /**
     * @return  A Map of flags that control the behavior of the User module.
     */
    Map<String, Boolean> getDomainFlags();

    /**
     * @return  True if registration is disabled, false if not.
     */
    boolean isRegistrationDisabled();

    /**
     * @return  The URI to redirect the user to after registration is there is not a saved request.
     */
    String getRegistrationSuccessURI();

    /**
     * @return  The URI to redirect the user to after they logout. Defaults to {@code /}.
     */
    String getLogoutURI();

    /**
     * @return  The URI of the login page. Defaults to {@code /login}.
     */
    String getLogingURI();

    /**
     * @return  The URI that the user is redirected to after a successful login and if there isn't
     *          a saved request. This defaults to {@code /}
     */
    String getLogingSuccessURI();
}