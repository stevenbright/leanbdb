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

package com.sleepycat.je.recovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sleepycat.je.Transaction;

public class RecoveryDuplicatesTest extends RecoveryTestBase {

    @Test
    public void testDuplicates()
        throws Throwable {

        createEnvAndDbs(1 << 20, true, NUM_DBS);
        int numRecs = 10;
        int numDups = N_DUPLICATES_PER_KEY;

        try {
            /* Set up an repository of expected data. */
            Map<TestData, Set<TestData>> expectedData = 
                new HashMap<TestData, Set<TestData>>();

            /* Insert all the data. */
            Transaction txn = env.beginTransaction(null, null);
            insertData(txn, 0, numRecs - 1, expectedData,
                       numDups, true, NUM_DBS);
            txn.commit();
            closeEnv();
            recoverAndVerify(expectedData, NUM_DBS);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void testDuplicatesWithDeletion()
        throws Throwable {

        createEnvAndDbs(1 << 20, true, NUM_DBS);
        int numRecs = 10;
        int nDups = N_DUPLICATES_PER_KEY;

        try {
            /* Set up an repository of expected data. */
            Map<TestData, Set<TestData>> expectedData = 
                new HashMap<TestData, Set<TestData>>();

            /* Insert all the data. */
            Transaction txn = env.beginTransaction(null, null);
            insertData(txn, 0, numRecs -1, expectedData, nDups, true, NUM_DBS);

            /* Delete all the even records. */
            deleteData(txn, expectedData, false, true, NUM_DBS);
            txn.commit();

            /* Modify all the records. */
            //    modifyData(expectedData);

            closeEnv();

            recoverAndVerify(expectedData, NUM_DBS);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void testDuplicatesWithAllDeleted()
        throws Throwable {

        createEnvAndDbs(1 << 20, true, NUM_DBS);
        int numRecs = 10;
        int nDups = N_DUPLICATES_PER_KEY;

        try {
            /* Set up an repository of expected data. */
            Map<TestData, Set<TestData>> expectedData = 
                new HashMap<TestData, Set<TestData>>();

            /* Insert all the data. */
            Transaction txn = env.beginTransaction(null, null);
            insertData(txn, 0, numRecs - 1, expectedData, nDups,
                       true, NUM_DBS);

            /* Delete all data. */
            deleteData(txn, expectedData, true, true, NUM_DBS);
            txn.commit();

            /* Modify all the records. */
            //    modifyData(expectedData);
            closeEnv();

            recoverAndVerify(expectedData, NUM_DBS);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }
}
