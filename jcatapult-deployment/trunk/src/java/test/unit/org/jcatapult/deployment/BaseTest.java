package org.jcatapult.deployment;

import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
@Ignore
public class BaseTest {
    protected Injector injector;

        /**
     * Sets up Guice and Configuration.
     */
    @Before
    public void setUp() {
        setUpGuice();
    }


    /**
     * Sets up the configuration and then the injector.
     */
    public void setUpGuice() {
        injector = Guice.createInjector();
        injector.injectMembers(this);
    }
}
