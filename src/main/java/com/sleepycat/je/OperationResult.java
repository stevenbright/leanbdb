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

package com.sleepycat.je;

/**
 * The result of an operation that successfully reads or writes a record.
 * <p>
 * An OperationResult does not contain any failure information. Methods that
 * perform unsuccessful reads or writes return null or throw an exception. Null
 * is returned if the operation failed for commonly expected reasons, such as a
 * read that fails because the key does not exist, or an insertion that fails
 * because the key does exist.
 * <p>
 * Methods that return OperationResult can be compared to methods that return
 * {@link OperationStatus} as follows: If {@link OperationStatus#SUCCESS} is
 * returned by the latter methods, this is equivalent to returning a non-null
 * OperationResult by the former methods.
 *
 * @since 7.0
 */
public class OperationResult {

    private final long expirationTime;

    OperationResult(final long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Returns the expiration time of the record, in milliseconds, or zero
     * if the record has no TTL and does not expire.
     * <p>
     * For 'get' operations, this is the expiration time of the current record.
     * For 'put operations, this is the expiration time of the newly written
     * record. For 'delete' operation, this is the expiration time of the
     * record that was deleted.
     * <p>
     * The return value will always be evenly divisible by the number of
     * milliseconds in one hour. If {@code TimeUnit.Days} was specified
     * when the record was written, the return value will also be evenly
     * divisible by the number of milliseconds in one day.
     *
     * @return the expiration time in milliseconds, or zero.
     */
    public long getExpirationTime() {
        return expirationTime;
    }
}
