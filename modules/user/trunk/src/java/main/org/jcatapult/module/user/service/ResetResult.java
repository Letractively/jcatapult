package org.jcatapult.module.user.service;

/**
 * <p>
 * This class is the result from a password reset.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ResetResult {
    private final boolean userMissing;

    public ResetResult(boolean userMissing) {
        this.userMissing = userMissing;
    }

    public boolean isUserMissing() {
        return userMissing;
    }
}