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

/** Test the DoubleExpMovingAvg class. */
public class DoubleExpMovingAvgTest extends TestBase {

    private DoubleExpMovingAvg avg;

    @Before
    public void setUp()
        throws Exception {

        super.setUp();
        avg = new DoubleExpMovingAvg("stat", 3000);
    }

    @Test
    public void testConstructorPeriodMillis() {
        avg.add(1, 1000);
        avg.add(2, 2000);
        avg.add(4, 3000);
        avg.add(8, 4000);
        assertEquals(3.7, avg.get(), 0.1);

        /* Shorter period skews result towards later entries */
        avg = new DoubleExpMovingAvg("stat", 2000);
        avg.add(1, 1000);
        avg.add(2, 2000);
        avg.add(4, 3000);
        avg.add(8, 4000);
        assertEquals(4.6, avg.get(), 0.1);
    }

    @Test
    public void testCopyConstructor() {
        avg.add(2, 1000);
        assertEquals(2, avg.get(), 0);
        DoubleExpMovingAvg copy = new DoubleExpMovingAvg(avg);
        assertEquals(avg.get(), copy.get(), 0);
        copy.add(4, 2000);
        assertEquals(2, avg.get(), 0);
        assertEquals(2.5, copy.get(), 0.1);
    }

    @Test
    public void testGetAndAdd() {
        assertEquals(0, avg.get(), 0);

        avg.add(1, 1000);
        assertEquals(1, avg.get(), 0);
        avg.add(4.2, 2000);
        assertEquals(2, avg.get(), 0.1);
        avg.add(5.5, 3000);
        assertEquals(3, avg.get(), 0.1);
        avg.add(3, 4000);
        assertEquals(3, avg.get(), 0.1);
        avg.add(-0.3, 5000);
        assertEquals(2, avg.get(), 0.1);
        avg.add(-1.3, 6000);
        assertEquals(1, avg.get(), 0.1);
        avg.add(-2.4, 7000);
        assertEquals(0, avg.get(), 0.1);
        avg.add(0, 8000);
        assertEquals(0, avg.get(), 0.1);

        /* Ignore items at same and earlier times */
        avg.add(123, 8000);
        avg.add(456, 2000);
        assertEquals(0, avg.get(), 0.1);
    }

    @Test
    public void testGetFormattedValue() {
        assertEquals("unknown", avg.getFormattedValue(true));
        avg.add(10000, 1000);
        assertEquals("10,000", avg.getFormattedValue(true));
        assertEquals("10000.00", avg.getFormattedValue(false));

        /*
         * Probably don't want to add NaN values, since they will keep the
         * average as NaN from then on, but at least make sure that toString
         * doesn't do something weird in this case.
         */
        avg.add(Double.NaN, 2000);
        assertEquals("NaN", avg.getFormattedValue());
    }

    @Test
    public void testIsNotSet() {
        assertTrue(avg.isNotSet());
        avg.add(1, 1000);
        assertFalse(avg.isNotSet());
        avg.add(2, 2000);
        assertFalse(avg.isNotSet());
    }
}
