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

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * <p>
 * This class is a static nasty singleton hack so that the Guice injector
 * from the Struts2 ObjectFactory is accessible to the entire application
 * and J2EE classes like JSP taglibs and Servlets can be injected.
 * </p>
 *
 * <p>
 * However, this should never be used by any class that can be normally
 * injected! This is only for classes that are instantiated and managed
 * by other containers such as J2EE containers.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public final class InjectorContext {
    private static Injector injector;

    /**
     * A static injection point for the injector itself. This stores the injector for everyone to access
     * anywhere in the entire WORLD! Okay, just in the application. However, this should never be used
     * by any class that can be normally injected! This is only for classes that are instantiated and
     * managed by other containers such as J2EE containers.
     *
     * @param   injector The Guice Injector.
     */
    @Inject
    public static void setInjector(Injector injector) {
        InjectorContext.injector = injector;
    }

    /**
     * A static injection point for the injector itself. This returns the injector for everyone to access
     * anywhere in the entire WORLD! Okay, just in the application. However, this should never be used
     * by any class that can be normally injected! This is only for classes that are instantiated and
     * managed by other containers such as J2EE containers.
     *
     * @return  The injector or null if it hasn't been injected yet. Remember that this is normally
     *          injected after the initial call into the Struts2 FilterDispatcher has been made. If
     *          this is called before that, than Guice will not be setup and this will be null.
     */
    public static Injector getInjector() {
        return injector;
    }
}