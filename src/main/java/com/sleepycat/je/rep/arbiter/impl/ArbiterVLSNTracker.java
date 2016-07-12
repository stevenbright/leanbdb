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

package com.sleepycat.je.rep.arbiter.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.utilint.VLSN;

/**
 * This class is used to maintain two pieces of
 * persistent state. The replication group node identifier
 * of the Arbiter and a VLSN value that represents the
 * highest commit record VLSN the Arbiter has acknowledged.
 */
class ArbiterVLSNTracker {
    private final int VERSION = 1;
    private RandomAccessFile raf;
    private final File dataFile;
    private VLSN currentVLSN = VLSN.NULL_VLSN;
    private final int VERSION_OFFSET = 0;
    private final int NODEID_OFFSET = Integer.SIZE + VERSION_OFFSET;
    private final int DATA_OFFSET = Integer.SIZE + NODEID_OFFSET;
    private int nodeId = NameIdPair.NULL_NODE_ID;

    ArbiterVLSNTracker(File file) {
        dataFile = file;
        boolean fileExists = dataFile.exists();
        try {
            raf = new RandomAccessFile(dataFile, "rwd");
            if (fileExists) {
                if (readVersion() != VERSION) {
                    throw new RuntimeException(
                        "Arbiter data file does not have a supported " +
                        "version field " +
                        dataFile.getAbsolutePath());
                }
                nodeId = readNodeId();
                if (raf.length() > DATA_OFFSET) {
                    raf.seek(DATA_OFFSET);
                    currentVLSN = new VLSN(raf.readLong());
                }
            } else {
                writeVersion(VERSION);
                writeNodeIdInternal(nodeId);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to read the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
        catch (Exception e) {
            throw new RuntimeException(
                "Unable to open the Arbiter data file " +
                dataFile.getAbsolutePath() + " exception " + e.getMessage());
        }

    }

    public synchronized void writeNodeId(int id) {
        if (nodeId == id) {
            return;
        }
        writeNodeIdInternal(id);
    }

    public synchronized int getCachedNodeId() {
        return nodeId;
    }

    private void writeNodeIdInternal(int id) {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to write the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        try {
            raf.seek(NODEID_OFFSET);
            raf.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to write the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    private int readNodeId() {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to read the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        try {
            raf.seek(NODEID_OFFSET);
            return raf.readInt();
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to read the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    public synchronized void writeVersion(int id) {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to write the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }

        if (nodeId == id) {
            return;
        }
        try {
            raf.seek(VERSION_OFFSET);
            raf.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to write the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    private int readVersion() {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to read the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        try {
            raf.seek(VERSION_OFFSET);
            return raf.readInt();
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to write the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    public synchronized void write(VLSN value) {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to write the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        if (value.compareTo(currentVLSN) > 0) {
            currentVLSN = new VLSN(value.getSequence());
            try {
                raf.seek(DATA_OFFSET);
                raf.writeLong(currentVLSN.getSequence());
            } catch (IOException e) {
                throw new RuntimeException(
                    "Unable to write the Arbiter data file " +
                    dataFile.getAbsolutePath());
            }
        }
    }

    public synchronized void close() {
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException ignore) {
            } finally {
                raf = null;
            }
        }
    }

    public VLSN get() {
        return currentVLSN;
    }
}
