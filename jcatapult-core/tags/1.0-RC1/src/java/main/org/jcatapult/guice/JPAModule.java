package org.jcatapult.guice;

import javax.persistence.EntityManager;

import org.jcatapult.jpa.EntityManagerProvider;

/**
 * <p>
 * This module should be used when JPA is required. This sets up the EntityManager
 * instances for injection using the {@link org.jcatapult.jpa.EntityManagerContext}
 * thread local. This thread local is setup by the
 * {@link org.jcatapult.servlet.JCatapultFilter} in web applications and
 * needs to be setup manually in other applications.
 * </p>
 *
 * @author  Brian Pontarelli and James Humphrey
 */
public class JPAModule extends ConfigurationModule {
    /**
     * Calls super then configures JPA.
     */
    @Override
    protected void configure() {
        super.configure();
        configureJPA();
    }

    /**
     * Sets up the JPA {@link org.jcatapult.jpa.EntityManagerProvider}.
     */
    protected void configureJPA() {
        bind(EntityManager.class).toProvider(new EntityManagerProvider());
    }
}
