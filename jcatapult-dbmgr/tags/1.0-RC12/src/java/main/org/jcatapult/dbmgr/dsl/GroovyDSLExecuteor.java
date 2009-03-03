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
package org.jcatapult.dbmgr.dsl;

import java.io.File;
import java.io.IOException;

import org.jcatapult.dbmgr.database.Dialect;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

/**
 * <p>
 * This class is just a messing around with Groovy as the DSL.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class GroovyDSLExecuteor {
    private final Dialect dialect;

    public GroovyDSLExecuteor(Dialect dialect) {
        this.dialect = dialect;
    }

    public String execute(File file) throws IOException, IllegalAccessException, InstantiationException {
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class klass = gcl.parseClass(file);
        GroovyObject object = (GroovyObject) klass.newInstance();
        MetaClass metaClass = new DSLMetaClass(klass, dialect);
        metaClass.initialize();
        object.setMetaClass(metaClass);

        dialect.startScript();
        object.invokeMethod("run", new Object[0]);
        return dialect.endScript();
    }
}
