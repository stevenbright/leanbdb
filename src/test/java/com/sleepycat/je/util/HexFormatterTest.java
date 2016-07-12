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

package com.sleepycat.je.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sleepycat.je.utilint.HexFormatter;
import com.sleepycat.util.test.TestBase;

/**
 * Trivial formatting class that sticks leading 0's on the front of a hex
 * number.
 */
public class HexFormatterTest extends TestBase {
    
    @Test
    public void testFormatLong() {
        assertTrue(HexFormatter.formatLong(0).equals("0x0000000000000000"));
        assertTrue(HexFormatter.formatLong(1).equals("0x0000000000000001"));
        assertTrue(HexFormatter.formatLong(0x1234567890ABCDEFL).equals("0x1234567890abcdef"));
        assertTrue(HexFormatter.formatLong(0x1234567890L).equals("0x0000001234567890"));
        assertTrue(HexFormatter.formatLong(0xffffffffffffffffL).equals("0xffffffffffffffff"));
    }
}
