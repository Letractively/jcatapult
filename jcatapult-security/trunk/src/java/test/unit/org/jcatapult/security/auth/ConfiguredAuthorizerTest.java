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
package org.jcatapult.security.auth;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.config.DefaultSecurityConfiguration;
import org.junit.Test;

import net.java.util.CollectionTools;

import static org.junit.Assert.*;

/**
 * <p> This tests the configured authorizer. </p>
 *
 * @author Brian Pontarelli
 */
public class ConfiguredAuthorizerTest {
  @Test
  public void testDirectURL() {
    Object user = new Object();
    Object user2 = new Object();
    UserAdapter ua = makeUserAdapter(user, user2, "admin");
    Configuration c = makeConfiguration("/admin=admin");
    ConfiguredAuthorizer ca = new ConfiguredAuthorizer(ua, new DefaultSecurityConfiguration(c));
    ca.authorize(user, "/admin");
    ca.authorize(user, "/admin2"); // not a restricted url
    ca.authorize(user, "/admin/resource"); // not a restricted url
    ca.authorize(user, "/admin/resource/resource2"); // not a restricted url

    ca.authorize(user2, "/admin2"); // not a restricted url
    ca.authorize(user2, "/admin/resource"); // not a restricted url
    ca.authorize(user2, "/admin/resource/resource2"); // not a restricted url

    try {
      ca.authorize(user2, "/admin");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    ca.authorize(null, "/admin2"); // not a restricted url
    ca.authorize(null, "/admin/resource"); // not a restricted url
    ca.authorize(null, "/admin/resource/resource2"); // not a restricted url

    try {
      ca.authorize(null, "/admin");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    EasyMock.verify(c, ua);
  }

  @Test
  public void testSubURL() {
    Object user = new Object();
    Object user2 = new Object();
    UserAdapter ua = makeUserAdapter(user, user2, "admin");
    Configuration c = makeConfiguration("/admin*=admin");
    ConfiguredAuthorizer ca = new ConfiguredAuthorizer(ua, new DefaultSecurityConfiguration(c));
    ca.authorize(user, "/admin");
    ca.authorize(user, "/admin2");
    ca.authorize(user, "/admin/resource"); // not restricted
    ca.authorize(user, "/admin/resource/resource2"); // not restricted

    ca.authorize(user2, "/admin/resource"); // not restricted
    ca.authorize(user2, "/admin/resource/resource2"); // not restricted

    try {
      ca.authorize(user2, "/admin");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    try {
      ca.authorize(user2, "/admin2");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    ca.authorize(null, "/admin/resource"); // not restricted
    ca.authorize(null, "/admin/resource/resource2"); // not restricted

    try {
      ca.authorize(null, "/admin");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    try {
      ca.authorize(null, "/admin2");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    EasyMock.verify(c, ua);
  }

  @Test
  public void testSubWithSlashURL() {
    Object user = new Object();
    Object user2 = new Object();
    UserAdapter ua = makeUserAdapter(user, user2, "admin");
    Configuration c = makeConfiguration("/admin/*=admin");
    ConfiguredAuthorizer ca = new ConfiguredAuthorizer(ua, new DefaultSecurityConfiguration(c));
    ca.authorize(user, "/admin"); // Not restricted
    ca.authorize(user, "/admin2"); // Not restricted
    ca.authorize(user, "/admin/resource");
    ca.authorize(user, "/admin/resource/resource2"); // Not restricted

    ca.authorize(user2, "/admin"); // Not restricted
    ca.authorize(user2, "/admin2"); // Not restricted
    ca.authorize(user2, "/admin/resource/resource2"); // Not restricted

    try {
      ca.authorize(user2, "/admin/resource");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    ca.authorize(null, "/admin"); // Not restricted
    ca.authorize(null, "/admin2"); // Not restricted
    ca.authorize(null, "/admin/resource/resource2"); // Not restricted

    try {
      ca.authorize(null, "/admin/resource");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    EasyMock.verify(c, ua);
  }

  @Test
  public void testNestedURL() {
    Object user = new Object();
    Object user2 = new Object();
    UserAdapter ua = makeUserAdapter(user, user2, "admin");
    Configuration c = makeConfiguration("/admin**=admin");
    ConfiguredAuthorizer ca = new ConfiguredAuthorizer(ua, new DefaultSecurityConfiguration(c));
    ca.authorize(user, "/admin");
    ca.authorize(user, "/admin2");
    ca.authorize(user, "/admin/resource");
    ca.authorize(user, "/admin/resource/resource2");

    try {
      ca.authorize(user2, "/admin");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    try {
      ca.authorize(user2, "/admin2");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    try {
      ca.authorize(user2, "/admin/resource");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    try {
      ca.authorize(user2, "/admin/resource/resource2");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      // expect
    } catch (NotLoggedInException e) {
      fail("Should not have thrown this");
    }

    try {
      ca.authorize(null, "/admin");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    try {
      ca.authorize(null, "/admin2");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    try {
      ca.authorize(null, "/admin/resource");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    try {
      ca.authorize(null, "/admin/resource/resource2");
      fail("Should have failed");
    } catch (AuthorizationException e) {
      fail("Should not have thrown this");
    } catch (NotLoggedInException e) {
      // expect
    }

    EasyMock.verify(c, ua);
  }

  private UserAdapter makeUserAdapter(Object user, Object user2, String... roles) {
    Set<String> rolesSet = CollectionTools.set(roles);
    UserAdapter ua = EasyMock.createStrictMock(UserAdapter.class);
    EasyMock.expect(ua.getRoles(user)).andReturn(rolesSet);
    EasyMock.expect(ua.getRoles(user)).andReturn(rolesSet);
    EasyMock.expect(ua.getRoles(user)).andReturn(rolesSet);
    EasyMock.expect(ua.getRoles(user)).andReturn(rolesSet);
    EasyMock.expect(ua.getRoles(user2)).andReturn(new HashSet<String>());
    EasyMock.expect(ua.getRoles(user2)).andReturn(new HashSet<String>());
    EasyMock.expect(ua.getRoles(user2)).andReturn(new HashSet<String>());
    EasyMock.expect(ua.getRoles(user2)).andReturn(new HashSet<String>());
    EasyMock.replay(ua);
    return ua;
  }

  private Configuration makeConfiguration(String config) {
    Configuration c = EasyMock.createStrictMock(Configuration.class);
    EasyMock.expect(c.getStringArray("jcatapult.security.authorization.rules")).andReturn(new String[]{config});
    EasyMock.replay(c);
    return c;
  }
}