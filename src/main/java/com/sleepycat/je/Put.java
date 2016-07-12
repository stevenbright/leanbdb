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

import com.sleepycat.je.dbi.PutMode;
import com.sleepycat.je.utilint.ThroughputStatGroup;

/**
 * The operation type passed to "put" methods on databases and cursors.
 */
public enum Put {

    /**
     * Inserts or updates a record depending on whether a matching record is
     * already present.
     *
     * <p>If the database does not have duplicate keys, a matching record is
     * defined as one with the same key. The existing record's data will be
     * replaced. In addition, if a custom key comparator is configured, and the
     * key bytes are different but considered equal by the comparator, the key
     * is replaced.</p>
     *
     * <p>If the database does have duplicate keys, a matching record is
     * defined as one with the same key and data. As above, if a custom key
     * comparator is configured, and the key bytes are different but considered
     * equal by the comparator, the key is replaced. In addition, if a custom
     * duplicate comparator is configured, and the data bytes are different but
     * considered equal by the comparator, the data is replaced.</p>
     *
     * <p>The operation always succeeds (null is never returned).</p>
     */
    OVERWRITE(
        PutMode.OVERWRITE,
        ThroughputStatGroup.DB_PUT_OFFSET,
        ThroughputStatGroup.CURSOR_PUT_OFFSET),

    /**
     * Inserts a record if a record with a matching key is not already present.
     *
     * <p>If the database has duplicate keys, a record is inserted only if
     * there are no records with a matching key.</p>
     *
     * <p>The operation does not succeed (null is returned) when an existing
     * record matches.</p>
     */
    NO_OVERWRITE(
        PutMode.NO_OVERWRITE,
        ThroughputStatGroup.DB_PUTNOOVERWRITE_OFFSET,
        ThroughputStatGroup.CURSOR_PUTNOOVERWRITE_OFFSET),

    /**
     * Inserts a record in a database with duplicate keys if a record with a
     * matching key and data is not already present.
     *
     * <p>This operation is not allowed for databases that do not have
     * duplicate keys.</p>
     *
     * <p>The operation does not succeed (null is returned) when an existing
     * record matches.</p>
     */
    NO_DUP_DATA(
        PutMode.NO_DUP_DATA,
        ThroughputStatGroup.DB_PUTNODUPDATA_OFFSET,
        ThroughputStatGroup.CURSOR_PUTNODUPDATA_OFFSET),

    /**
     * Updates the data of the record at the cursor position.
     *
     * <p>If the database does not have duplicate keys, the existing record's
     * data will be replaced.</p>
     *
     * <p>If the database does have duplicate keys, the existing data is
     * replaced but it is must be considered equal by the duplicate comparator.
     * If the data is not considered equal, {@link DuplicateDataException} is
     * thrown. Using the default comparator, a key is considered equal only if
     * its bytes are equal. Therefore, changing the data is only possible if a
     * custom duplicate comparator is configured.</p>
     *
     * <p>A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for partial data update.</p>
     *
     * <p>This operation cannot be used to update the key of an existing record
     * and in fact the key parameter must be null when calling generic put
     * methods such as
     * {@link Database#put(Transaction, DatabaseEntry, DatabaseEntry, Put,
     * WriteOptions)} and
     * {@link Cursor#put(DatabaseEntry, DatabaseEntry, Put, WriteOptions)}.</p>
     *
     * <p>The operation does not succeed (null is returned) if the record at
     * the current position has been deleted. This can occur in two cases: 1.
     * If the record was deleted using this cursor and then accessed. 2. If the
     * record was not locked by this cursor or transaction, and was deleted by
     * another thread or transaction after this cursor was positioned on
     * it.</p>
     */
    CURRENT(
        PutMode.CURRENT,
        -1,
        ThroughputStatGroup.CURSOR_PUTCURRENT_OFFSET);

    private final PutMode putMode;
    private final int dbStat;
    private final int cursorStat;

    Put(final PutMode putMode, final int dbStat, final int cursorStat) {
        this.putMode = putMode;
        this.dbStat = dbStat;
        this.cursorStat = cursorStat;
    }

    PutMode getPutMode() {
        return putMode;
    }

    int getDbStat() {
        return dbStat;
    }

    int getCursorStat() {
        return cursorStat;
    }
}
