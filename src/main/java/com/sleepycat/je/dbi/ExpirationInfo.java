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

package com.sleepycat.je.dbi;

import java.util.concurrent.TimeUnit;

import com.sleepycat.je.WriteOptions;

/**
 * A struct for passing record expiration info to a 'put' operation, and
 * returning the old expiration time plus whether it was updated/changed.
 */
public class ExpirationInfo {

    public static final ExpirationInfo DEFAULT =
        new ExpirationInfo(0, false, false);

    public final int expiration;
    public final boolean expirationInHours;
    public final boolean updateExpiration;
    private boolean expirationUpdated = false;
    private long oldExpirationTime = 0;

    public ExpirationInfo(
        final int expiration,
        final boolean expirationInHours,
        final boolean updateExpiration) {

        this.expiration = expiration;
        this.expirationInHours = expirationInHours;
        this.updateExpiration = updateExpiration;
    }

    /**
     * Creates an ExpirationInfo struct from the WriteOptions TTL params, for
     * the current system time.
     *
     * @param options WriteOptions, may not be null.
     *
     * @return ExpirationInfo, or null if WriteOptions.getTTL is zero and
     * WriteOptions.getUpdateTTL is false, meaning we will not add or update
     * the TTL.
     */
    public static ExpirationInfo getInfo(final WriteOptions options) {

        if (options.getTTL() == 0 && !options.getUpdateTTL()) {
            return null;
        }

        return new ExpirationInfo(
            TTL.ttlToExpiration(options.getTTL(), options.getTTLUnit()),
            options.getTTLUnit() == TimeUnit.HOURS,
            options.getUpdateTTL());
    }

    public void setExpirationUpdated(boolean val) {
        expirationUpdated = val;
    }

    public boolean getExpirationUpdated() {
        return expirationUpdated;
    }

    public void setOldExpirationTime(long val) {
        oldExpirationTime = val;
    }

    public long getOldExpirationTime() {
        return oldExpirationTime;
    }
}
