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

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

import org.jcatapult.security.UserAdapter;
import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

/**
 * <p>
 * This class implements the Authorizer interface and uses the JCatapult
 * configuration system in order to determine which resources are accessible
 * by which roles. The configuration parameter that controls the authorization
 * information is named:
 * </p>
 *
 * <pre>
 * jcatapult.security.authorization
 * </pre>
 *
 * <p>
 * This parameter uses a simple format of the authorization information
 * for a single resource per line. Each line has this format:
 * </p>
 *
 * <pre>
 * resource=roles
 * </pre>
 *
 * <p>
 * The resource definition is either the full resource string or a wildcard
 * that contains either * or ** at the end. The * indicates that all of the
 * resources in the given directory are matched and the ** means all resources
 * in the given directory and all sub-directories are matched. This is only
 * applicable to web resources. For example:
 * </p>
 *
 * <pre>
 * /foo matches /foo only
 * /foo* matches /foo and /foobar
 * /foo/* matches /foo/a and /foo/b
 * /foo** matches /foo, /foobar, /foo/a and /foo/a/b
 * /foo/** matches /foo/a and /foo/a/b
 * </pre>
 *
 * <p>
 * If no configuration is specified, the default is:
 * </p>
 *
 * <pre>
 * /admin**=admin
 * </pre>
 *
 * @author Brian Pontarelli
 */
public class ConfiguredAuthorizer implements Authorizer {
    private static final Logger logger = Logger.getLogger(ConfiguredAuthorizer.class.getName());
    private final UserAdapter userAdapter;
    private final List<ResourceAuth> resourceAuths = new ArrayList<ResourceAuth>();

    @Inject
    public ConfiguredAuthorizer(UserAdapter userAdapter, Configuration configuration) {
        this.userAdapter = userAdapter;
        String[] rules = configuration.getString("jcatapult.security.authorization.rules", "/admin**=admin").trim().split("\n");
        for (String rule : rules) {
            String[] parts = rule.trim().split("=");
            if (parts.length != 2) {
                throw new RuntimeException("Invalid authorization configuration rule [" + rule +
                    "] in the JCatapult configuration files");
            }

            String uri = parts[0];
            boolean wildCard = uri.endsWith("*");
            boolean subWildCard = uri.endsWith("**");
            if (subWildCard) {
                wildCard = false;
                uri = uri.substring(0, uri.length() - 2);
            } else if (wildCard) {
                uri = uri.substring(0, uri.length() - 1);
            }

            String[] roles = parts[1].split(",");
            ResourceAuth resourceAuth = new ResourceAuth(uri, wildCard, subWildCard, new HashSet<String>(Arrays.asList(roles)));
            resourceAuths.add(resourceAuth);
        }
    }

    public void authorize(Object user, String resource) throws AuthorizationException, NotLoggedInException {
        Set<String> roles = user != null ? userAdapter.getRoles(user) : null;
        logger.finest("Authorizing user for roles [" + roles + "]");
        for (ResourceAuth resourceAuth : resourceAuths) {
            boolean equal = resource.equals(resourceAuth.resource);
            if (equal || (resource.startsWith(resourceAuth.resource) && (resourceAuth.dirWildcard || resourceAuth.subDirWildcard))) {

                // Check the resource for wildcard cases that fail like /foo* and /foo/bar
                if (!equal && resourceAuth.dirWildcard && resource.indexOf("/", resourceAuth.resource.length()) != -1) {
                    continue;
                }

                // If the resource requires some roles and the user is not logged in, throw an exception
                if (user == null) {
                    throw new NotLoggedInException();
                }

                // Check the roles
                boolean found = false;
                for (String role : resourceAuth.roles) {
                    if (roles.contains(role)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new AuthorizationException();
                }
            }
        }
    }

    public static class ResourceAuth {
        final String resource;
        final boolean dirWildcard;
        final boolean subDirWildcard;
        final Set<String> roles;

        public ResourceAuth(String resource, boolean dirWildcard, boolean subDirWildcard, Set<String> roles) {
            this.resource = resource;
            this.dirWildcard = dirWildcard;
            this.subDirWildcard = subDirWildcard;
            this.roles = roles;
        }
    }
}