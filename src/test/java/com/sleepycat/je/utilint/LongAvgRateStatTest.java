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

package com.sleepycat.je.utilint;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sleepycat.util.test.TestBase;

/** Test the LongAvgRateStat class. */
public class LongAvgRateStatTest extends TestBase {

    private static final StatGroup statGroup =
        new StatGroup("TestGroup", "Test group");
    private static int statDefCount;

    private LongAvgRateStat stat;

    @Before
    public void setUp()
        throws Exception {

        super.setUp();
        stat = new LongAvgRateStat(
            statGroup, getStatDef(), 3000, MILLISECONDS);
    }

    private static StatDefinition getStatDef() {
        return new StatDefinition(getStatDefName(), "");
    }

    private static String getStatDefName() {
        return "stat" + Integer.toString(++statDefCount);
    }

    @Test
    public void testCopy() {
        stat.add(0, 1000);
        stat.add(3000, 2000);
        LongAvgRateStat copy = stat.copy();
        stat.add(9000, 3000);
        copy.add(15000, 3000);
        assertEquals(Long.valueOf(4), stat.get());
        assertEquals(Long.valueOf(6), copy.get());
    }

    @Test
    public void testComputeInterval() {
        LongAvgRateStat other = stat.copy();

        LongAvgRateStat interval = stat.computeInterval(other);
        assertTrue(interval.isNotSet());

        stat.add(0, 1000);
        stat.add(3000, 2000);
        interval = stat.computeInterval(other);
        assertEquals(Long.valueOf(3), interval.get());
        interval = other.computeInterval(stat);
        assertEquals(Long.valueOf(3), interval.get());

        other.add(10000, 4000);
        other.add(40000, 5000);
        interval = stat.computeInterval(other);
        assertEquals(Long.valueOf(20), interval.get());
        interval = other.computeInterval(stat);
        assertEquals(Long.valueOf(20), interval.get());

        stat.clear();
        other.clear();
        stat.add(10000, 1000);
        stat.add(40000, 2000);
        other.add(0, 4000);
        other.add(3000, 5000);
        interval = stat.computeInterval(other);
        assertEquals(Long.valueOf(13), interval.get());
        interval = other.computeInterval(stat);
        assertEquals(Long.valueOf(13), interval.get());
    }
}
