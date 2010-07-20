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
package org.jcatapult.mvc.action.result.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jcatapult.mvc.action.result.HeaderResult;

/**
 * <p>
 * This annotation marks a result from an action as a header only result.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ResultAnnotation(HeaderResult.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Header {
    /**
     * @return  The result code from the action's execute method that this Result is associated with.
     */
    String code() default "success";

    /**
     * @return  The status code.
     */
    int status() default 200;

    /**
     * @return  The status code as a String so that it can be expanded.
     */
    String statusStr() default "";
}