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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sleepycat.util.test.TestBase;

/** Test the LongDiffStat class. */
public class LongDiffStatTest extends TestBase {

    private static final StatGroup statGroup =
        new StatGroup("TestGroup", "Test group");
    private static int statDefCount;

    private AtomicLongStat base;
    private LongDiffStat stat;

    @Before
    public void setUp()
        throws Exception {

        super.setUp();
        base = new AtomicLongStat(statGroup, getStatDef());
        base.set(1000L);
        stat = new LongDiffStat(base, 3000);
    }

    private static StatDefinition getStatDef() {
        return new StatDefinition(getStatDefName(), "");
    }

    private static String getStatDefName() {
        return "stat" + Integer.toString(++statDefCount);
    }

    @Test
    public void testGet() {
        assertEquals(0, stat.get(1000));
        stat.set(300, 1000);
        base.set(2000L);
        assertEquals(700, stat.get(2000));
        assertEquals(1700, stat.get(5000));
        stat.set(3000, 6000);
        assertEquals(0, stat.get(7000));
    }

    @Test
    public void testClear() {
        stat.set(10, 1000);
        assertEquals(990, stat.get(1000));
        assertFalse(stat.isNotSet());
        stat.clear();
        assertEquals(0, stat.get(1000));
        assertTrue(stat.isNotSet());
    }

    @Test
    public void testCopy() {
        stat.set(300, 1000);
        LongDiffStat copy = stat.copy();
        stat.set(350, 2000);
        base.set(2000L);
        assertEquals(700, copy.get(1000));
        copy.set(400, 3000);
        assertEquals(650, stat.get(3000));
    }

    @Test
    public void testGetFormattedValue() {
        base.set(123456790L);
        stat.set(1, System.currentTimeMillis());
        assertEquals("123,456,789", stat.getFormattedValue(true));
        assertEquals("123456789", stat.getFormattedValue(false));
    }

    @Test
    public void testIsNotSet() {
        assertTrue(stat.isNotSet());
        stat.set(200, 1000);
        assertFalse(stat.isNotSet());
        stat.clear();
        assertTrue(stat.isNotSet());
    }
}
