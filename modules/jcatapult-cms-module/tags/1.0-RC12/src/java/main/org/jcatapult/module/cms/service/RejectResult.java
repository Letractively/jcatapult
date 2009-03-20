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
 *
 */
package org.jcatapult.module.cms.service;

import org.jcatapult.module.cms.domain.Node;

/**
 * <p>
 * This class is the result of a node rejection.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class RejectResult implements Result {
    private final Node node;
    private final boolean rejected;

    public RejectResult(Node node, boolean rejected) {
        this.node = node;
        this.rejected = rejected;
    }

    public Node getNode() {
        return node;
    }

    public boolean isRejected() {
        return rejected;
    }

    public boolean isPending() {
        return false;
    }

    public boolean isSuccess() {
        return node != null && rejected;
    }
}