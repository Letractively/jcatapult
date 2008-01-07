/*
 * Copyright (c) 2001-2006, JCatapult.org, All Rights Reserved
 */
package org.jcatapult.acegi;

import org.acegisecurity.GrantedAuthority;

/**
 * <p>
 * This is a test adapter.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class TestUserAdapter implements ACEGIUserAdapter {
    public String getUsername(Object user) {
        return ((TestUser) user).getUsername();
    }

    public String getPassword(Object user) {
        return ((TestUser) user).getPassword();
    }

    public boolean isExpired(Object user) {
        return false;
    }

    public boolean isLocked(Object user) {
        return false;
    }

    public boolean isDisabled(Object user) {
        return false;
    }

    public boolean areCredentialsExpired(Object user) {
        return false;
    }

    public GrantedAuthority[] getGrantedAuthorities(Object user) {
        return new GrantedAuthority[0];
    }
}