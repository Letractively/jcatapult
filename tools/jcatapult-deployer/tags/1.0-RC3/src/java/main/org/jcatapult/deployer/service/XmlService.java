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

package org.jcatapult.deployer.service;

import java.io.File;

/**
 * <p>Interface service for unmarshalling xml</p>
 *
 * @author jhumphrey
 */
public interface XmlService<T> {

    /**
     * <p>Unmarshall's a file into a type T</p>
     *
     * @param file the file to unmarshall
     * @return type T the type to return after unmarshalling
     * @throws XmlServiceException thrown if there's an error during the xml unmarshalling process
     */
    public T unmarshall(File file) throws XmlServiceException;
}
