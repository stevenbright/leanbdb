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
package com.sleepycat.je.rep.arbiter;

import java.io.Serializable;

import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_MASTER;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_N_ACKS;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_N_REPLAY_QUEUE_OVERFLOW;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_STATE;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_VLSN;

import com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition;
import com.sleepycat.je.utilint.StatGroup;

/*
 * @hidden
 */
public class ArbiterStats implements Serializable {

    private static final long serialVersionUID = 1734048134L;

    private final StatGroup arbStats;

    /**
     * @hidden
     * Internal use only.
     */
    ArbiterStats(StatGroup arbGrp) {
        if (arbGrp != null) {
            arbStats = arbGrp;
        } else {
            arbStats = new StatGroup(ArbiterStatDefinition.GROUP_NAME,
                    ArbiterStatDefinition.GROUP_DESC);
        }
    }

    /**
     * The number of attempts to queue a response when
     * the queue was full.
     */
    public long getReplayQueueOverflow() {
        return arbStats.getLong(ARB_N_REPLAY_QUEUE_OVERFLOW);
    }

    /**
     * The number of transactions that has been
     * acknowledged.
     */
    public long getAcks() {
        return arbStats.getLong(ARB_N_ACKS);
    }

    /**
     * The current master node.
     */
    public String getMaster() {
        return arbStats.getString(ARB_MASTER);
    }

    /**
     * The ReplicatedEnvironment.State of the node.
     */
    public String getState() {
        return arbStats.getString(ARB_STATE);
    }

    /**
     * The highest commit VLSN that has been
     * acknowledged.
     */
    public long getVLSN() {
        return arbStats.getLong(ARB_VLSN);
    }
}

