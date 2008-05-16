package org.jcatapult.deployment.guice;

import org.jcatapult.deployment.service.BetterSimpleCompletor;

import com.google.inject.AbstractModule;
import jline.Completor;

/**
 * Simple guice module for performing project binds for dependency injection
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeploymentModule extends AbstractModule {

    protected void configure() {
        bind(Completor.class).to(BetterSimpleCompletor.class);
    }
}
