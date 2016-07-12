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

package com.sleepycat.je.txn;

import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.Timestamp;

/**
 * The base class for records that mark the end of a transaction.
 */
public abstract class TxnEnd implements Loggable {

    long id;
    Timestamp time;
    long lastLsn;

    /* For replication - master node which wrote this record. */
    int repMasterNodeId;

    TxnEnd(long id, long lastLsn, int repMasterNodeId) {
        this.id = id;
        time = new Timestamp(System.currentTimeMillis());
        this.lastLsn = lastLsn;
        this.repMasterNodeId = repMasterNodeId;
    }

    /**
     * For constructing from the log
     */
    public TxnEnd() {
        lastLsn = DbLsn.NULL_LSN;
    }

    /*
     * Accessors.
     */
    public long getId() {
        return id;
    }

    public Timestamp getTime() {
        return time;
    }

    long getLastLsn() {
        return lastLsn;
    }

    public int getMasterNodeId() {
        return repMasterNodeId;
    }

    public long getTransactionId() {
        return id;
    }

    protected abstract String getTagName();
}
