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
package org.jcatapult.dbmgr.service;

import java.util.Queue;

import org.jcatapult.dbmgr.domain.Artifact;
import org.jcatapult.dbmgr.domain.SQLScript;

import com.google.inject.ImplementedBy;

/**
 * Defines an interface for implementing a strategy for
 * sorting {@link SQLScript} objects based on its version
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
@ImplementedBy(ArtifactScriptVersionSortStrategyImpl.class)
public interface ArtifactScriptVersionSortStrategy {

    /**
     * Sorts artifact sql scripts into a queue where the first in is the first out
     *
     * @param artifact {@link org.jcatapult.dbmgr.domain.Artifact}
     * @param seedOnly true if sorting seed scripts only, false otherwise
     * @return a sorted set of {@link org.jcatapult.dbmgr.domain.SQLScript} objects
     */
    Queue<SQLScript> sort(Artifact artifact, boolean seedOnly);
}
