/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
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

package org.jcatapult.deployer.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import com.jcraft.jsch.UserInfo;
import net.java.lang.reflect.ReflectionTools;

/**
 * <p>Bean that contains user info for JSCH integration</p>
 *
 * @author jhumphrey
 */
public class BaseUserInfo implements UserInfo {
    private String password;
    private String passphrase;
    private boolean trust;

    /**
     * <p>Constructor</p>
     *
     * @param password the password
     * @param passphrase the passphrase.  can be empty
     * @param trust true to trust, false otherwise
     */
    public BaseUserInfo(String password, String passphrase, boolean trust) {
        this.password = password;
        this.passphrase = passphrase;
        this.trust = trust;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        return password;
    }

    public boolean promptPassword(String string) {
        if (password != null) {
            return true;
        }

        System.out.print(string);
        System.out.print(" ");
        password = readLine(true);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean promptPassphrase(String string) {
        if (passphrase != null) {
            return true;
        }

        System.out.print(string);
        System.out.print(" ");
        passphrase = readLine(true);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean promptYesNo(String string) {
        if (trust) {
            return true;
        }

        System.out.print(string);
        System.out.print(" ");
        String answer = readLine(false);
        return answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y");
    }

    /**
     * {@inheritDoc}
     */
    public void showMessage(String string) {
        System.out.println(string);
    }

    /**
     * <p>Helper method for reading input from the command line</p>
     *
     * @param hidden true if hidden, false otherwise
     * @return the line
     */
    private String readLine(boolean hidden) {
        if (!hidden) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                return br.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        try {
            Class consoleClass = Class.forName("java.io.Console");
            Method consoleMethod = ReflectionTools.findMethod(System.class, "console");
            Object instance = ReflectionTools.invokeMethod(consoleMethod, null);
            if (instance == null) {
                throw new IllegalStateException("The JVM doesn't have a console and SSH requires user input");
            }

            Method passwordMethod = ReflectionTools.findMethod(consoleClass, "readPassword");
            return new String((char[]) ReflectionTools.invokeMethod(passwordMethod, instance));
        } catch (ClassNotFoundException cnfe) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                return br.readLine();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
