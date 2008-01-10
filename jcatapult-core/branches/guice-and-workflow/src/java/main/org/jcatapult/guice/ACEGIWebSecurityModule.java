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
package org.jcatapult.guice;

import org.acegisecurity.providers.dao.SaltSource;
import org.acegisecurity.providers.dao.salt.SystemWideSaltSource;
import org.acegisecurity.providers.encoding.Md5PasswordEncoder;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.jcatapult.acegi.ACEGIUserAdapter;
import org.jcatapult.security.AuthenticationService;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Provider;

/**
 * <p>
 * This is the default abstract module that most web applications that
 * use JCatapult Security will extend. This forces sub-classes to provide
 * a {@link org.jcatapult.acegi.ACEGIUserAdapter} in order to
 * setup the JCatapult Security framework properly.
 * </p>
 *
 * @author  James Humphrey and Brian Pontarelli
 */
public abstract class ACEGIWebSecurityModule extends WebModule {
    /**
     * Calls super and then calls these methods in this order:
     *
     * <ol>
     * <li>{@link super#configure()}</li>
     * <li>{@link #configureACEGI()}</li>
     * <li>{@link #configureSecurityProvider()}</li>
     * </ol>
     */
    protected void configure() {
        super.configure();

        configureACEGI();
        configureSecurityProvider();
    }

    /**
     * Configures the {@link org.jcatapult.security.SecurityContext} so that it correctly has a SecurityContextProvider
     * injected. This is done via static injection on the {@link org.jcatapult.security.SecurityContext} class.
     */
    protected void configureSecurityProvider() {
        // Initialize the static ACEGI security provider
        requestStaticInjection(SecurityContext.class);
    }

    /**
     * Configures the ACEGI classes necessary for registration. This is usually a {@link PasswordEncoder}
     * which is an MD5 password encoder by default and a {@link SaltSource}, which is a system wide
     * salt source. This method invokes the {@link #salt()} method to fetch the salt. Sub-classes
     * should implement that method to provide a better salt.
     */
    protected void configureACEGI() {
        // Setup the ACEGI classes
        bind(PasswordEncoder.class).toProvider(new Provider<PasswordEncoder>() {
            public PasswordEncoder get() {
                Md5PasswordEncoder encoder = new Md5PasswordEncoder();
                encoder.setEncodeHashAsBase64(true);
                return encoder;
            }
        });
        bind(SaltSource.class).toProvider(new Provider<SaltSource>() {
            public SaltSource get() {
                SystemWideSaltSource ss = new SystemWideSaltSource();
                ss.setSystemWideSalt(salt());
                return ss;
            }
        });

        // Use the template method to get the security provider
        bind(ACEGIUserAdapter.class).to(getACEGIUserAdapter());
        bind(AuthenticationService.class).to(getAuthenticationService());
    }

    /**
     * Returns the implementation class for the {@link org.jcatapult.security.AuthenticationService}
     * interface that the application provides to JCatapult.
     *
     * @return  The AuthenticationService implementation class.
     */
    protected abstract Class<? extends AuthenticationService> getAuthenticationService();

    /**
     * Returns the implementation class for the {@link org.jcatapult.acegi.ACEGIUserAdapter}
     * interface that the application provides to JCatapult.
     *
     * @return  The ACEGIUserAdapter implementation class.
     */
    protected abstract Class<? extends ACEGIUserAdapter> getACEGIUserAdapter();

    /**
     * @return  Returns the default salt of <strong>jcatapult</strong>.
     */
    protected String salt() {
        return "jcatapult";
    }
}