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

import java.util.List;

import net.java.error.Error;
import net.java.error.ErrorList;
import net.java.validate.Validatable;

/**
 * <p>Concrete validation service that uses the java net commons Validatable
 * interface to perform validation on domain object</p>
 *
 * <p>This service delegates to the actual domain objects to perform the validation</p>
 *
 * @author jhumphrey
 */
public class ValidationServiceImpl<T extends Validatable> implements ValidationService<T> {

    /**
     * {@inheritDoc}
     */
    public String validate(T props) {
        ErrorList errorList = props.validate();

        // build a buffer with all the error messages.  this will be empty if no error messages exist
        StringBuffer errorBuffer = new StringBuffer();
        List<net.java.error.Error> errors = errorList.getAllErrors();
        for (Error error : errors) {
            errorBuffer.append(error.toString()).append("\n");
        }

        return errorBuffer.toString();
    }
}
