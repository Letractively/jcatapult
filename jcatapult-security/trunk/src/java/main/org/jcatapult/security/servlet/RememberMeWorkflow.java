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
package org.jcatapult.security.servlet;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import net.java.io.FileTools;

import com.google.inject.Inject;

/**
 * <p> This class implements a simple remember me service that if activated will load the users information from a
 * persistent cookie. This uses a client side cookie with a lightweight encryption to provide some level of security.
 * The encryption is two-way and based on a file that is read in that contains the encryption key. This is somewhat
 * brute force, but it is the only way to ensure that cookies are not easily hacked. </p> <p/> <p> Therefore, if you
 * want to use remember me services, you'll need to specify this configuration parameter in the JCatapult configuration
 * files: </p> <p/>
 * <pre>
 * jcatapult.security.rememberme.encryption.file
 * </pre>
 * <p/> <p> If this parameter is not setup, remember me services will be disabled. </p>
 *
 * @author Brian Pontarelli
 */
public class RememberMeWorkflow {
  private static final String KEY = "jcatapult.security.rememberme.encryption.file";

  @Inject
  public RememberMeWorkflow(Configuration configuration) {
    String fileName = configuration.getString(KEY);
    if (fileName != null) {
      try {
        String encryptionKey = FileTools.read(fileName).toString().trim();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}