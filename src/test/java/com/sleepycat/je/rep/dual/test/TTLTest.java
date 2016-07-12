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

package com.sleepycat.je.rep.dual.test;

import java.util.List;

import org.junit.runners.Parameterized;

public class TTLTest extends com.sleepycat.je.test.TTLTest {

    @Parameterized.Parameters
    public static List<Object[]> genParams() {
        return getTxnParams(null, true /*rep*/);
    }

    public TTLTest(String type){
        super(type);
    }

    /*
     * Causes a data mismatch when comparing nodes, because the test only
     * compresses BINs on the master node and the time is set artificially.
     */
    @Override
    public void testCompression() {}

    /*
     * LNs are not purged as expected with replication, because .jdb files are
     * protected from deletion for various reasons, e.g., a temporary replica
     * lag.
     */
    @Override
    public void testPurgedLNs() {}

    /*
     * Same as for testPurgedLNs.
     */
    @Override
    public void testPurgedSlots() {}
}
