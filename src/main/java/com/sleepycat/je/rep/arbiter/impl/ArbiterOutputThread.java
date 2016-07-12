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

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.ReplicaOutputThreadBase;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.stream.Protocol;
import com.sleepycat.je.utilint.VLSN;

/**
 * The ArbiterOutputThread reads transaction identifiers
 * from the outputQueue and writes a acknowledgment
 * response to to the network channel. Also used
 * to write responses for heart beat messages.
 */
public class ArbiterOutputThread extends ReplicaOutputThreadBase {
    private final ArbiterVLSNTracker vlsnTracker;

    public ArbiterOutputThread(RepImpl repImpl,
                               BlockingQueue<Long> outputQueue,
                               Protocol protocol,
                               DataChannel replicaFeederChannel,
                               ArbiterVLSNTracker vlsnTracker) {
        super(repImpl, null, outputQueue, protocol, replicaFeederChannel);
        this.vlsnTracker = vlsnTracker;
    }

    public void writeHeartbeat(Long txnId) throws IOException {
        VLSN vlsn = vlsnTracker.get();
        protocol.write(protocol.new HeartbeatResponse
                (vlsn,
                 vlsn),
                 replicaFeederChannel);
    }
}
