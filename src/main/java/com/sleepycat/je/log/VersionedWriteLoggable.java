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

package com.sleepycat.je.log;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.sleepycat.je.log.entry.ReplicableLogEntry;

/**
 * A sub-interface of {@link Loggable} implemented by classes that can write
 * themselves to a byte buffer in an earlier log format, for use by instances
 * of {@link ReplicableLogEntry} that need to support an earlier log format
 * during replication.  See [#22336].
 *
 * <p>Classes that implement {@code Loggable} should implement this interface
 * if they are included in replication data.
 *
 * <p>Implementing classes should document the version of the class's most
 * recent format change.  Log entry classes that contain {@code
 * VersionedWriteLoggable} items can use that information to determine if they
 * can copy the log contents for an entry directly or if they need to convert
 * them in order to be compatible with a particular log version.
 */
public interface VersionedWriteLoggable extends Loggable {

    /**
     * Returns the log version of the most recent format change for this
     * loggable item.
     *
     * @return the log version of the most recent format change
     *
     * @see ReplicableLogEntry#getLastFormatChange()
     */
    int getLastFormatChange();

    /**
     * @see ReplicableLogEntry#getEmbeddedLoggables()
     */
    Collection<VersionedWriteLoggable> getEmbeddedLoggables();

    /**
     * Returns the number of bytes needed to store this object in the format
     * for the specified log version.  Earlier log versions only need to be
     * supported for log entries with format changes made in {@link
     * LogEntryType#LOG_VERSION_REPLICATE_OLDER} or greater.
     *
     * @param logVersion the log version
     * @param forReplication whether the entry will be sent over the wire,
     * and not written to the log.
     * @return the number of bytes to store this object for the log version
     */
    int getLogSize(int logVersion, boolean forReplication);

    /**
     * Serializes this object into the specified buffer in the format for the
     * specified log version.  Earlier log versions only need to be
     * supported for log entries with format changes made in {@link
     * LogEntryType#LOG_VERSION_REPLICATE_OLDER} or greater.
     *
     * @param logBuffer the destination buffer
     * @param logVersion the log version
     * @param forReplication whether the entry will be sent over the wire,
     * and not written to the log.
     */
    void writeToLog(ByteBuffer logBuffer,
                    int logVersion,
                    boolean forReplication);

    /**
     * Returns whether this format has a variant that is optimized for
     * replication.
     */
    boolean hasReplicationFormat();

    /**
     * Returns whether it is worthwhile to materialize and then re-serialize a
     * log entry in a format optimized for replication. Implementations should
     * attempt to check efficiently, without instantiating the log entry
     * object. Some implementations will simply return false.
     *
     * <p>WARNING: The logBuffer position must not be changed by this method.
     *
     * <p>WARNING: The shared LogEntry object is used for calling this method,
     * and this method must not change any of the fields in the object.
     *
     * @param logBuffer contains the entry that would be re-serialized.
     * @param srcVersion the log version of entry in logBuffer.
     * @param destVersion the version that would be used for re-serialization.
     */
    boolean isReplicationFormatWorthwhile(ByteBuffer logBuffer,
                                          int srcVersion,
                                          int destVersion);
}
