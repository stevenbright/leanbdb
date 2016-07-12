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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.log.entry.LogEntry;
import com.sleepycat.je.log.entry.ReplicableLogEntry;
import com.sleepycat.util.test.TestBase;

public class LogEntryTest extends TestBase {

    @Test
    public void testEquality()
        throws DatabaseException {

        byte testTypeNum = LogEntryType.LOG_IN.getTypeNum();

        /* Look it up by type */
        LogEntryType foundType = LogEntryType.findType(testTypeNum);
        assertEquals(foundType, LogEntryType.LOG_IN);
        assertTrue(foundType.getSharedLogEntry() instanceof
                   com.sleepycat.je.log.entry.INLogEntry);

        /* Look it up by type */
        foundType = LogEntryType.findType(testTypeNum);
        assertEquals(foundType, LogEntryType.LOG_IN);
        assertTrue(foundType.getSharedLogEntry() instanceof
                   com.sleepycat.je.log.entry.INLogEntry);

        /* Get a new entry object */
        LogEntry sharedEntry = foundType.getSharedLogEntry();
        LogEntry newEntry = foundType.getNewLogEntry();

        assertTrue(sharedEntry != newEntry);
    }

    /**
     * See {@link ReplicableLogEntry#getEmbeddedLoggables()}.
     */
    @Test
    public void testLastFormatChange() throws Exception {
        for (final LogEntryType type : LogEntryType.getAllTypes()) {
            final LogEntry entry = type.getSharedLogEntry();
            if (!(entry instanceof ReplicableLogEntry)) {
                continue;
            }
            final ReplicableLogEntry repEntry = (ReplicableLogEntry) entry;
            verifyLastFormatChange(
                repEntry.getClass().getName(), repEntry.getLastFormatChange(),
                repEntry.getEmbeddedLoggables());
        }
    }

    private void verifyLastFormatChange(
        final String entryClassName,
        final int entryLastFormatChange,
        final Collection<VersionedWriteLoggable> embeddedLoggables)
        throws Exception {

        assertNotNull(embeddedLoggables);

        if (embeddedLoggables.size() == 0) {
            return;
        }

        for (final VersionedWriteLoggable child : embeddedLoggables) {

            final int childLastFormatChange = child.getLastFormatChange();

            if (childLastFormatChange > entryLastFormatChange) {
                fail(String.format(
                    "Embedded %s version %d is GT entry %s version %d",
                    child.getClass().getName(), childLastFormatChange,
                    entryClassName, entryLastFormatChange));
            }

            verifyLastFormatChange(
                entryClassName, entryLastFormatChange,
                child.getEmbeddedLoggables());
        }
    }
}
