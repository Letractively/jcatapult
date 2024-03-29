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
package org.jcatapult.security.saved;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This Class is the saved request.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SavedHttpRequest {
    public final String uri;
    public final Map<String, String[]> parameters;

    public SavedHttpRequest(String uri, Map<String, String[]> parameters) {
        this.uri = uri;
        this.parameters = new HashMap<String, String[]>();
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedHttpRequest that = (SavedHttpRequest) o;

        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null)
            return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }
}