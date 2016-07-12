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

import org.junit.Test;

import com.sleepycat.util.test.TestBase;

/** Test the AtomicLongComponent class */
public class AtomicLongComponentTest extends TestBase {

    @Test
    public void testConstructor() {
        AtomicLongComponent comp = new AtomicLongComponent();
        assertEquals(Long.valueOf(0), comp.get());
    }

    @Test
    public void testSet() {
        AtomicLongComponent comp = new AtomicLongComponent();
        comp.set(72);
        assertEquals(Long.valueOf(72), comp.get());
    }

    @Test
    public void testClear() {
        AtomicLongComponent comp = new AtomicLongComponent();
        comp.set(37);
        comp.clear();
        assertEquals(Long.valueOf(0), comp.get());
    }

    @Test
    public void testCopy() {
        AtomicLongComponent comp = new AtomicLongComponent();
        comp.set(70);
        AtomicLongComponent copy = comp.copy();
        comp.clear();
        assertEquals(Long.valueOf(70), copy.get());
        copy.set(75);
        assertEquals(Long.valueOf(0), comp.get());
    }

    @Test
    public void testGetFormattedValue() {
        AtomicLongComponent comp = new AtomicLongComponent();
        comp.set(123456789);
        assertEquals("123,456,789", comp.getFormattedValue(true));
        assertEquals("123456789", comp.getFormattedValue(false));
    }

    @Test
    public void testIsNotSet() {
        AtomicLongComponent comp = new AtomicLongComponent();
        assertTrue(comp.isNotSet());
        comp.set(3);
        assertFalse(comp.isNotSet());
        comp.clear();
        assertTrue(comp.isNotSet());
    }

    @Test
    public void testToString() {
        AtomicLongComponent comp = new AtomicLongComponent();
        comp.set(987654321);
        assertEquals("987654321", comp.toString());
    }
}
