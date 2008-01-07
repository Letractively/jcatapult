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

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.jcatapult.security.spi.SecurityContextProvider;

import com.google.inject.Inject;

/**
 * <p>
 * This class implements the {@link SecurityContextProvider} using the ACEGI
 * security information. This makes a number of assumptions about how ACEGI
 * is being used. First off it assumes that the details Object is a
 * {@link UserDetails}. This is an ACEGI API that providers access to the
 * user name and other information.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ACEGISecurityContextProvider implements SecurityContextProvider {
    private ACEGIUserAdapter userAdapter;

    @Inject
    public ACEGISecurityContextProvider(ACEGIUserAdapter userAdapter) {
        this.userAdapter = userAdapter;
    }

    /**
     * @return  The username from the ACEGI SecurityContext if it exists. Otherwise this will return
     *          the String <code>anonymous</code>.
     */
    public String getCurrentUsername() {
        Object user = getCurrentUser();
        if (user != null) {
            return userAdapter.getUsername(user);
        }

        return "anonymous";
    }


    /**
     * Fetches the User object from the ACEGI security context's Authentication object. This is always
     * the applications User object and NOT the ACEGI UserDetails if the application is correctly
     * setup to use the {@link org.jcatapult.acegi.JCatapultAuthenticationProvider}. If the
     * Authentication is anonymous, this will return null.
     *
     * @return  The user object.
     */
    public Object getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication auth = context.getAuthentication();
            if (auth instanceof AnonymousAuthenticationToken) {
                return null;
            }

            return auth.getPrincipal();
        }

        return null;
    }

    /**
     * Logs the user into the application. This assumes that the given user object is an instance of
     * the UserDetails interface.
     *
     * @param   user The user to login.
     */
    public void login(Object user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user,
            userAdapter.getUsername(user), userAdapter.getGrantedAuthorities(user));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }

    /**
     * Logs the user out via the SecurityContext.
     */
    public void logout() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
    }

    /**
     * Since ACEGI just uses a simple thread local. This just calls login, because that method doesn't
     * do any extra work.
     *
     * @param   user The new user instance.
     */
    public void update(Object user) {
        login(user);
    }
}