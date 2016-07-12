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

import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;

public class ArbiterStatDefinition {

    public static final String GROUP_NAME = "Arbiter";
    public static final String GROUP_DESC =
        "Arbiter statistics";

    public static final StatDefinition ARB_N_REPLAY_QUEUE_OVERFLOW =
        new StatDefinition(
            "nReplayQueueOverflow",
            "The number of times replay queue failed to insert " +
             "because if was full.");

    public static final StatDefinition ARB_N_ACKS =
        new StatDefinition(
            "nAcks",
             "The number of transactions acknowledged.");

    public static final StatDefinition ARB_MASTER =
        new StatDefinition(
            "master",
            "The current or last Master Replication Node the Arbiter accessed.",
            StatType.CUMULATIVE);

    public static final StatDefinition ARB_STATE =
            new StatDefinition(
                 "state",
                 "The current state of the Arbiter.",
                 StatType.CUMULATIVE);

    public static final StatDefinition ARB_VLSN =
            new StatDefinition(
                "vlsn",
                "The highest VLSN that was acknowledged by the Arbiter.",
                StatType.CUMULATIVE);
}
