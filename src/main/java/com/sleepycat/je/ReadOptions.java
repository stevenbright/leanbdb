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

import static com.sleepycat.je.EnvironmentFailureException.unexpectedException;

import com.sleepycat.je.utilint.DatabaseUtil;

/**
 * Options for calling methods that read records.
 *
 * @since 7.0
 */
public class ReadOptions implements Cloneable {

    private CacheMode cacheMode = null;
    private LockMode lockMode = LockMode.DEFAULT;

    /**
     * Constructs a ReadOptions object with default values for all properties.
     */
    public ReadOptions() {
    }

    @Override
    public ReadOptions clone() {
        try {
            return (ReadOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw unexpectedException(e);
        }
    }

    /**
     * Sets the {@code CacheMode} to be used for the operation.
     * <p>
     * By default this property is null, meaning that the default specified
     * using {@link Cursor#setCacheMode},
     * {@link DatabaseConfig#setCacheMode} or
     * {@link EnvironmentConfig#setCacheMode} will be used.
     *
     * @param cacheMode is the {@code CacheMode} used for the operation, or
     * null to use the Cursor, Database or Environment default.
     *
     * @return 'this'.
     */
    public ReadOptions setCacheMode(final CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    /**
     * Returns the {@code CacheMode} to be used for the operation, or null
     * if the Cursor, Database or Environment default will be used.
     *
     * @see #setCacheMode(CacheMode)
     */
    public CacheMode getCacheMode() {
        return cacheMode;
    }

    /**
     * Sets the {@code LockMode} to be used for the operation.
     * <p>
     * By default this property is {@link LockMode#DEFAULT}.
     *
     * @param lockMode the locking attributes. Specifying null or
     * {@link LockMode#READ_COMMITTED} is not allowed.
     *
     * @return 'this'.
     */
    public ReadOptions setLockMode(final LockMode lockMode) {
        DatabaseUtil.checkForNullParam(lockMode, "lockMode");
        this.lockMode = lockMode;
        return this;
    }

    /**
     * Returns the {@code LockMode} to be used for the operation.
     *
     * @see #setLockMode(LockMode)
     */
    public LockMode getLockMode() {
        return lockMode;
    }
}
