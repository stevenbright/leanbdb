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

package com.sleepycat.je.rep.utilint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sleepycat.je.txn.Txn;

/**
 * Test operation of the simple map.
 */
public class SimpleTxnMapTest {

    @Test
    public void testBasic() {
        try {
            @SuppressWarnings("unused")
            SimpleTxnMap<TestTxn> ignore = new SimpleTxnMap<TestTxn>(10);
            fail("Expected ISE");
        } catch (IllegalArgumentException ise) {
            /* Expected. */
        }
        final int arrayMapSize = 128;
        SimpleTxnMap<TestTxn> m = new SimpleTxnMap<TestTxn>(arrayMapSize);
        Map<Long, TestTxn> rm = new HashMap<>();

        check(rm, m);

        for (long i=0; i < arrayMapSize; i++) {
            TestTxn t = new TestTxn(i);
            rm.put(i, t);
            m.put(t);
            check(rm, m);
        }

        assertEquals(0, m.getBackupMap().size());

        /* create holes in array map */
        for (long i=0; i < arrayMapSize; i+=2) {
            TestTxn t1 = rm.remove(i);
            TestTxn t2 = m.remove(i);
            assertEquals(t1, t2);
            check(rm, m);
        }

        assertEquals(0, m.getBackupMap().size());

        /* Use emptied array slots and create backup map entries. */
        for (long i=arrayMapSize; i < arrayMapSize*2; i++) {
            TestTxn t = new TestTxn(i);
            rm.put(i, t);
            m.put(t);
            check(rm, m);
        }
        /* 1/2 the entries should be in the backup map. */
        assertEquals(arrayMapSize/2, m.getBackupMap().size());

        /* Remove some more entries and check */
        for (long i=1; i < arrayMapSize; i+=2) {
            TestTxn t1 = rm.remove(i);
            TestTxn t2 = m.remove(i);
            assertEquals(t1, t2);
            check(rm, m);
        }

        /* Check for truly long txn ids. */
        for (long i=Integer.MAX_VALUE * 2l;
             i < ((Integer.MAX_VALUE * 2l) + arrayMapSize);
             i++) {
            TestTxn t = new TestTxn(i);
            rm.put(i, t);
            m.put(t);
            check(rm, m);
        }

        rm.clear();
        m.clear();
        check(rm, m);
    }

    private static final int testSize = 100000000;

    /**
     * A very rough way to ensure that the code path is shorter with the simple
     * map. Runs show a 2-4X perf improvement over map.
     *
     * There's a benefit in terms of heap allocation as well which would
     * translate into request latency improvements due to less frequent new
     * space gcs. The mbean output can be used to confirm the heap benefits.
     *
     * This test is normally turned off. It should be run by hand in isolation
     * to get a handle on the perf benefits.
     */
    public void OfftestSimpleMapPerf() {

        final int arrayMapSize = 128;
        TestTxn t = new TestTxn(5);

        List<MemoryPoolMXBean> mbeans = ManagementFactory.getMemoryPoolMXBeans();
        dumpHeapUsage(mbeans);
        final SimpleTxnMap<TestTxn> m = new SimpleTxnMap<TestTxn>(arrayMapSize);
        long startMs = System.currentTimeMillis();
        for (int i=0; i <  testSize; i++) {
            m.put(t);
            m.get(t.getId());
            m.remove(t.getId());
        }
        long endMs = System.currentTimeMillis();
        dumpHeapUsage(mbeans);

        System.err.println("Elapsed time simple map:" + (endMs - startMs));

        final Map<Long, TestTxn> rm =
            Collections.synchronizedMap(new HashMap<Long, TestTxn>());
        startMs = System.currentTimeMillis();
        for (int i=0; i <  testSize; i++) {
            rm.put(t.getId(), t);
            rm.get(t.getId());
            rm.remove(t.getId());
        }
        endMs = System.currentTimeMillis();
        System.err.println("Elapsed time java map:" + (endMs - startMs));
        dumpHeapUsage(mbeans);
    }

    private void dumpHeapUsage(List<MemoryPoolMXBean> mbeans) {
        System.err.println("Heap usage:");
        for (MemoryPoolMXBean mb : mbeans) {
            if (mb.getType() == MemoryType.HEAP) {
                System.err.println(mb.getName() + " peak:" + mb.getPeakUsage());
                mb.resetPeakUsage();
            }
        }
    }

    private class TestTxn extends Txn {
        TestTxn(long id) {
            this.id = id;
        }
    }

    private void check(Map<Long, TestTxn> rm, SimpleTxnMap<TestTxn> m) {
        assertEquals(rm.size(), m.size());
        assertEquals(rm.isEmpty(), m.isEmpty());

        for (TestTxn rmt : rm.values()) {
            assertEquals(rmt, m.get(rmt.getId()));
        }
    }
}
