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

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.VersionedWriteLoggable;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.util.PackedInteger;

/**
 * Based class for commit and abort records, which are replicated.
 * The log formats for commit and abort are identical.
 */
public abstract class VersionedWriteTxnEnd
    extends TxnEnd implements VersionedWriteLoggable {

    /**
     * The log version of the most recent format change for this loggable.
     *
     * @see #getLastFormatChange
     */
    private static final int LAST_FORMAT_CHANGE = 8;

    VersionedWriteTxnEnd(long id, long lastLsn, int masterId) {
        super(id, lastLsn, masterId);
    }

    /**
     * For constructing from the log.
     */
    public VersionedWriteTxnEnd() {
    }

    /*
     * Log support for writing.
     */

    @Override
    public int getLastFormatChange() {
        return LAST_FORMAT_CHANGE;
    }

    @Override
    public Collection<VersionedWriteLoggable> getEmbeddedLoggables() {
        return Collections.emptyList();
    }

    @Override
    public int getLogSize() {
        return getLogSize(LogEntryType.LOG_VERSION, false /*forReplication*/);
    }

    @Override
    public void writeToLog(final ByteBuffer logBuffer) {
        writeToLog(
            logBuffer, LogEntryType.LOG_VERSION, false /*forReplication*/);
    }

    @Override
    public int getLogSize(final int logVersion, final boolean forReplication) {
        return LogUtils.getPackedLongLogSize(id) +
            LogUtils.getTimestampLogSize(time) +
            LogUtils.getPackedLongLogSize(
                forReplication ? DbLsn.NULL_LSN : lastLsn) +
            LogUtils.getPackedIntLogSize(repMasterNodeId);
    }

    @Override
    public void writeToLog(final ByteBuffer logBuffer,
                           final int entryVersion,
                           final boolean forReplication) {
        if (entryVersion >= 12) {
            LogUtils.writePackedLong(logBuffer,
                forReplication ? DbLsn.NULL_LSN : lastLsn);
        }
        LogUtils.writePackedLong(logBuffer, id);
        LogUtils.writeTimestamp(logBuffer, time);
        if (entryVersion < 12) {
            LogUtils.writePackedLong(logBuffer,
                forReplication ? DbLsn.NULL_LSN : lastLsn);
        }
        LogUtils.writePackedInt(logBuffer, repMasterNodeId);
    }

    public void readFromLog(ByteBuffer logBuffer, int entryVersion) {
        boolean isUnpacked = (entryVersion < 6);
        if (entryVersion >= 12) {
            lastLsn = LogUtils.readLong(logBuffer, isUnpacked);
        }
        id = LogUtils.readLong(logBuffer, isUnpacked);
        time = LogUtils.readTimestamp(logBuffer, isUnpacked);
        if (entryVersion < 12) {
            lastLsn = LogUtils.readLong(logBuffer, isUnpacked);
        }
        if (entryVersion >= 6) {
            repMasterNodeId = LogUtils.readInt(logBuffer,
                false /* unpacked */);
        }
    }

    @Override
    public boolean hasReplicationFormat() {
        return true;
    }

    @Override
    public boolean isReplicationFormatWorthwhile(final ByteBuffer logBuffer,
                                                 final int srcVersion,
                                                 final int destVersion) {
        /*
         * It is too much trouble to parse versions older than 12, because the
         * lastLsn is not at the front in older versions.
         */
        if (srcVersion < 12) {
            return false;
        }

        /*
         * If the size of lastLsn is greater than one (meaning it is not
         * NULL_LSN), then we should re-serialize.
         */
        return PackedInteger.getReadLongLength(
            logBuffer.array(),
            logBuffer.arrayOffset() + logBuffer.position()) > 1;
    }

    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<").append(getTagName());
        sb.append(" id=\"").append(id);
        sb.append("\" time=\"").append(time);
        sb.append("\" master=\"").append(repMasterNodeId);
        sb.append("\">");
        sb.append(DbLsn.toString(lastLsn));
        sb.append("</").append(getTagName()).append(">");
    }
}
