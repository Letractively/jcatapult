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
package org.jcatapult.acegi;

import org.acegisecurity.AccountExpiredException;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.CredentialsExpiredException;
import org.acegisecurity.DisabledException;
import org.acegisecurity.LockedException;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.SaltSource;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.jcatapult.security.AuthenticationService;

/**
 * <p>
 * This class is an authentication provider that allows JCatapult to use ACEGI
 * without the UserDetails class. That class is difficult to manage and work
 * with and it is much better to allow direct access to the user object at
 * all times.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class JCatapultAuthenticationProvider implements AuthenticationProvider {
    private SaltSource saltSource;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;
    private ACEGIUserAdapter adapter;

    public JCatapultAuthenticationProvider(SaltSource saltSource, PasswordEncoder passwordEncoder,
            ACEGIUserAdapter adapter, AuthenticationService authenticationService) {
        this.saltSource = saltSource;
        this.passwordEncoder = passwordEncoder;
        this.adapter = adapter;
        this.authenticationService = authenticationService;
    }

    /**
     * Uses the adapter for everything. Gets the user from the adapter, gets the password from the
     * user via the adapter, verifies the password (not using the adapter), checks all the flags using
     * the adapter.
     *
     * @param   authentication The authentication token to authenticate.
     * @return  The authentication if the passed in authentication was authenticated.
     * @throws  AuthenticationException If something is wrong.
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        Object user = authenticationService.loadUser((String) token.getPrincipal(), null);
        if (user == null) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        String password = adapter.getPassword(user);
        String inputPassword = (String) token.getCredentials();
        String encodedInputPassword = passwordEncoder.encodePassword(inputPassword, saltSource.getSalt(null));
        if (password != null && !password.equals(encodedInputPassword)) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        if (adapter.isExpired(user)) {
            throw new AccountExpiredException("The account has expired.");
        } else if (adapter.isLocked(user)) {
            throw new LockedException("Account is locked.");
        } else if (adapter.isDisabled(user)) {
            throw new DisabledException("The account is disabled.");
        } else if (adapter.areCredentialsExpired(user)) {
            throw new CredentialsExpiredException("The credentials have expired.");
        }

        return new UsernamePasswordAuthenticationToken(user, password, adapter.getGrantedAuthorities(user));
    }

    /**
     * Only supports the UsernamePasswordAuthenticationToken.
     *
     * @param   authentication The auth.
     * @return  True if it is the UsernamePasswordAuthenticationToken.
     */
    public boolean supports(Class authentication) {
        return (authentication == UsernamePasswordAuthenticationToken.class);
    }
}