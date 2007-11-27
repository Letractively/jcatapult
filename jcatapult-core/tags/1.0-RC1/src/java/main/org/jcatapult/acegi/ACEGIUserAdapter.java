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
package org.jcatapult.acegi;

import org.acegisecurity.GrantedAuthority;
import org.jcatapult.security.UserAdapter;

/**
 * <p>
 * This interface allows JCatapult to use ACEGI without the use of the slightly
 * painful UserDetails interface. Instead, this allows the application to work
 * directly with the user object of its choosing.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface ACEGIUserAdapter<T> extends UserAdapter<T> {
    /**
     * Returns the granted authorities for the user. This is the one interface we still need to use
     * because it is defined on the ACEGI authentication interface (sucks).
     *
     * @param   user The user to get the granted authorities for.
     * @return  The authorities for the user.
     */
    GrantedAuthority[] getGrantedAuthorities(T user);
}