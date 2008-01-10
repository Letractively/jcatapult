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
package org.jcatapult.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This class uses the JCE (Java Cryptography Extensions) to encode passwords.
 * This encryption uses a salt source that is retrieved using the {@link SaltSource}
 * interface.
 * </p>
 *
 * <p>
 * This class uses the Apache commons configuration API to configure how the
 * passwords are encrypted and encoded. The configuration values are:
 * </p>
 *
 * <table>
 * <tr><th>Name</th><th>Description</th><th>Default</th></tr>
 * <tr><td>jcatapult.security.passwordEncryptor.encodeBase64</td><td>Determines if
 *  the passwords are encoded into base 64 before they are returned.</td><td>true</td></tr>
 * <tr><td>jcatapult.security.passwordEncryptor.algorithm</td><td>The encryption
 *  algorithm to use. This must be a JCE message digest algorithm.</td><td>MD5</td></tr>
 * </table>
 *
 * @author Brian Pontarelli
 */
public class JCEPasswordEncryptor implements PasswordEncryptor<Object> {
    private final SaltSource saltSource;
    private final Configuration configuration;

    @Inject
    public JCEPasswordEncryptor(SaltSource saltSource, Configuration configuration) {
        this.saltSource = saltSource;
        this.configuration = configuration;
    }

    /**
     * Encodes the rawPass using a MessageDigest. If a salt is specified it will be merged with the
     * password before encoding.
     *
     * @param   password The plain text password.
     * @param   obj If this is not null, it is passed to the salt source. Otherwise, the default
     *          salt from the salt source is used.
     * @return  Hex string of password digest (or base64 encoded string if encodeHashAsBase64 is
     *          enabled).
     */
    public String encryptPassword(String password, Object obj) {
        String salt = obj == null ? saltSource.getSalt() : saltSource.getSalt(obj);
        String saltedPass = mergePasswordAndSalt(password, salt, false);
        MessageDigest messageDigest = getMessageDigest();

        byte[] digest = messageDigest.digest(saltedPass.getBytes());

        if (configuration.getBoolean("jcatapult.security.passwordEncryptor.encodeBase64", true)) {
            return new String(Base64.encodeBase64(digest));
        } else {
            return new String(Hex.encodeHex(digest));
        }
    }

    /**
     * Get a MessageDigest instance for the given algorithm.
     *
     * @return  The MessageDigest instance.
     * @throws  IllegalArgumentException If the algorithm configured is not valid.
     */
    protected final MessageDigest getMessageDigest() throws IllegalArgumentException {
        String algorithm = configuration.getString("jcatapult.security.passwordEncryptor.algorithm", "MD5");
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
        }
    }

    /**
     * Used by subclasses to generate a merged password and salt <code>String</code>.
     *
     * <P>
     * The generated password will be in the form of <code>password{salt}</code>.
     * </p>
     * <p>
     * A <code>null</code> can be passed to either method, and will be handled correctly. If the
     * <code>salt</code> is <code>null</code> or empty, the resulting generated password will simply
     * be the passed <code>password</code>. The <code>toString</code> method of the <code>salt</code>
     * will be used to represent the salt.
     * </p>
     *
     * @param password the password to be used (can be <code>null</code>)
     * @param salt the salt to be used (can be <code>null</code>)
     * @param strict ensures salt doesn't contain the delimiters
     * @return a merged password and salt <code>String</code>
     * @throws IllegalArgumentException if the salt contains '{' or '}' characters.
     */
    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        if (password == null) {
            password = "";
        }

        if (strict && (salt != null)) {
            if ((salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1)) {
                throw new IllegalArgumentException("Cannot use { or } in salt.toString()");
            }
        }

        if ((salt == null) || "".equals(salt)) {
            return password;
        } else {
            return password + "{" + salt.toString() + "}";
        }
    }
}