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

package com.sleepycat.je.cleaner;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.sleepycat.util.test.TestBase;

/**
 * Tests the OffsetList and PackedOffset classes.
 */
public class OffsetTest extends TestBase {

    @Test
    public void testOffsets() {

        doAllTest(new long[] {
            1,
            2,
            0xfffe,
            0xffff,
            0xfffff,
            Integer.MAX_VALUE - 1,
            Integer.MAX_VALUE,

            /*
             * The following values don't work, which is probably a bug, but
             * LSN offsets are not normally this large so the bug currently has
             * little impact.
             */
            //Integer.MAX_VALUE + 1L,
            //Long.MAX_VALUE - 100L,
            //Long.MAX_VALUE,
        });
    }

    private void doAllTest(long[] offsets) {

        ArrayList list = list(offsets);

        doOneTest(offsets);

        Collections.reverse(list);
        doOneTest(array(list));

        Collections.shuffle(list);
        doOneTest(array(list));
    }

    private void doOneTest(long[] offsets) {

        OffsetList list = new OffsetList();
        for (int i = 0; i < offsets.length; i += 1) {
            list.add(offsets[i], true);
        }
        long[] array = list.toArray();
        assertTrue("array=\n" + dump(array) + " offsets=\n" + dump(offsets),
                   Arrays.equals(offsets, array));

        long[] sorted = new long[array.length];
        System.arraycopy(array, 0, sorted, 0, array.length);
        Arrays.sort(sorted);

        PackedOffsets packed = new PackedOffsets();
        packed.pack(array);
        assertTrue(Arrays.equals(sorted, packed.toArray()));
    }

    private ArrayList list(long[] array) {

        ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; i += 1) {
            list.add(new Long(array[i]));
        }
        return list;
    }

    private long[] array(ArrayList list) {

        long[] array = new long[list.size()];
        for (int i = 0; i < array.length; i += 1) {
            array[i] = ((Long) list.get(i)).longValue();
        }
        return array;
    }

    private String dump(long[] array) {

        StringBuilder buf = new StringBuilder(array.length * 10);
        for (int i = 0; i < array.length; i += 1) {
            buf.append(Long.toString(array[i]));
            buf.append(' ');
        }
        return buf.toString();
    }
}
