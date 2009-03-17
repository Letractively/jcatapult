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
 *
 */
package org.jcatapult.module.simpleuser.service;

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
     * @return Whether or not emails are being verified.
     */
    boolean isVerifyEmails();

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