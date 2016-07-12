/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2016 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package com.sleepycat.persist;

import com.sleepycat.je.OperationResult;

/**
 * Used to return an entity value from a 'get' operation along with an
 * OperationResult. If the operation fails, null is returned. If the operation
 * succeeds and a non-null EntityResult is returned, the contained entity value
 * and OperationResult are guaranteed to be non-null.
 */
public class EntityResult<V> {

    private final V value;
    private final OperationResult result;

    EntityResult(V value, OperationResult result) {
        assert value != null;
        assert result != null;

        this.value = value;
        this.result = result;
    }

    /**
     * Returns the entity value resulting from the operation.
     *
     * @return the non-null entity value.
     */
    public V value() {
        return value;
    }

    /**
     * Returns the OperationResult resulting from the operation.
     *
     * @return the non-null OperationResult.
     */
    public OperationResult result() {
        return result;
    }
}
