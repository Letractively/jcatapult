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
package org.jcatapult.mvc.result.control;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * <p>
 * This class is a FreeMarker method that can be invoked within the
 * control templates to spit out all of the attributes of a tag. It
 * takes a single parameter that is a Map&lt;?, ?> and returns a
 * String that contains the name value pairs in the format:
 * </p>
 *
 * <pre>
 * key=&quot;value&quot;
 * </pre>
 *
 * @author  Brian Pontarelli
 */
public class AppendAttributesMethod implements TemplateMethodModelEx {
    public Object exec(List arguments) throws TemplateModelException {
        StringBuilder build = new StringBuilder();
        SimpleHash hash = (SimpleHash) arguments.get(0);
        Map<String, Object> map = hash.toMap();
        SortedSet<String> sortedKeys = new TreeSet<String>(map.keySet());
        for (String key : sortedKeys) {
            build.append(" ").append(key).append("=\"").append(map.get(key)).append("\"");
        }

        return new SimpleScalar(build.toString());
    }
}