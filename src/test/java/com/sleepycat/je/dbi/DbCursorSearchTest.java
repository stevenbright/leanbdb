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

package com.sleepycat.je.dbi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.util.StringDbt;
import com.sleepycat.utilint.StringUtils;

/**
 * Test cursor getSearch*
 */
public class DbCursorSearchTest extends DbCursorTestBase {

    public DbCursorSearchTest() {
        super();
    }

    /**
     * Put a small number of data items into the database
     * then make sure we can retrieve them with getSearchKey.
     */
    @Test
    public void testSimpleSearchKey()
        throws DatabaseException {
        initEnv(false);
        doSimpleCursorPuts();
        verify(simpleDataMap, false);
    }

    /**
     * Put a small number of data items into the database
     * then make sure we can retrieve them with getSearchKey.
     * Delete them, and make sure we can't search for them anymore.
     */
    @Test
    public void testSimpleDeleteAndSearchKey()
        throws DatabaseException {

        initEnv(false);
        doSimpleCursorPuts();
        verify(simpleDataMap, true);
    }

    /**
     * Put a large number of data items into the database,
     * then make sure we can retrieve them with getSearchKey.
     */
    @Test
    public void testLargeSearchKey()
        throws DatabaseException {

        initEnv(false);
        Hashtable expectedData = new Hashtable();
        doLargePut(expectedData, N_KEYS);
        verify(expectedData, false);
    }

    /**
     * Put a large number of data items into the database,
     * then make sure we can retrieve them with getSearchKey.
     */
    @Test
    public void testLargeDeleteAndSearchKey()
        throws DatabaseException {

        initEnv(false);
        Hashtable expectedData = new Hashtable();
        doLargePut(expectedData, N_KEYS);
        verify(expectedData, true);
    }

    @Test
    public void testLargeSearchKeyDuplicates()
        throws DatabaseException {

        initEnv(true);
        Hashtable expectedData = new Hashtable();
        createRandomDuplicateData(expectedData, false);

        verifyDuplicates(expectedData);
    }

    /**
     * Put a small number of data items into the database
     * then make sure we can retrieve them with getSearchKey.
     * See [#9337].
     */
    @Test
    public void testSimpleSearchBothWithPartialDbt()
        throws DatabaseException {

        initEnv(false);
        doSimpleCursorPuts();
        DatabaseEntry key = new DatabaseEntry(StringUtils.toUTF8("bar"));
        DatabaseEntry data = new DatabaseEntry(new byte[100]);
        data.setSize(3);
        System.arraycopy(StringUtils.toUTF8("two"), 0, data.getData(), 0, 3);
        OperationStatus status =
            cursor2.getSearchBoth(key, data, LockMode.DEFAULT);
        assertEquals(OperationStatus.SUCCESS, status);
    }

    @Test
    public void testGetSearchBothNoDuplicatesAllowedSR9522()
        throws DatabaseException {

        initEnv(false);
        doSimpleCursorPuts();
        DatabaseEntry key = new DatabaseEntry(StringUtils.toUTF8("bar"));
        DatabaseEntry data = new DatabaseEntry(StringUtils.toUTF8("two"));
        OperationStatus status =
            cursor2.getSearchBoth(key, data, LockMode.DEFAULT);
        assertEquals(OperationStatus.SUCCESS, status);
    }

    /**
     * Make sure the database contains the set of data we put in.
     */
    private void verify(Hashtable expectedData, boolean doDelete)
        throws DatabaseException {

        Iterator iter = expectedData.entrySet().iterator();
        StringDbt testKey = new StringDbt();
        StringDbt testData = new StringDbt();

        // Iterate over the expected values.
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            testKey.setString((String) entry.getKey());

            // search for the expected values using SET.
            OperationStatus status = cursor2.getSearchKey(testKey, testData,
                                                          LockMode.DEFAULT);
            assertEquals(OperationStatus.SUCCESS, status);
            assertEquals((String) entry.getValue(), testData.getString());
            assertEquals((String) entry.getKey(), testKey.getString());

            // check that getCurrent returns the same thing.
            status = cursor2.getCurrent(testKey, testData, LockMode.DEFAULT);
            assertEquals(OperationStatus.SUCCESS, status);
            assertEquals((String) entry.getValue(), testData.getString());
            assertEquals((String) entry.getKey(), testKey.getString());

            if (doDelete) {
                // Delete the entry and make sure that getSearchKey doesn't
                // return it again.
                status = cursor2.delete();
                assertEquals(OperationStatus.SUCCESS, status);

                // search for the expected values using SET.
                status =
                    cursor2.getSearchKey(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.NOTFOUND, status);

                // search for the expected values using SET_BOTH.
                status =
                    cursor2.getSearchBoth(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.NOTFOUND, status);

                // search for the expected values using SET_RANGE - should
                // give 0 except if this is the last key in the tree, in which
                // case DB_NOTFOUND.  It should never be DB_KEYEMPTY.
                // It would be nice to be definite about the expected
                // status, but to do that we have to know whether this is the
                // highest key in the set, which we don't currently track.
                status = cursor2.getSearchKeyRange
                    (testKey, testData, LockMode.DEFAULT);
                assertTrue(status == OperationStatus.SUCCESS ||
                           status == OperationStatus.NOTFOUND);
            } else {
                // search for the expected values using SET_BOTH.
                status =
                    cursor2.getSearchBoth(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals((String) entry.getValue(), testData.getString());
                assertEquals((String) entry.getKey(), testKey.getString());

                // check that getCurrent returns the same thing.
                status =
                    cursor2.getCurrent(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals((String) entry.getValue(), testData.getString());
                assertEquals((String) entry.getKey(), testKey.getString());

                // check that SET_RANGE works as expected for exact keys
                status = cursor2.getSearchKeyRange
                    (testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals((String) entry.getValue(), testData.getString());
                assertEquals((String) entry.getKey(), testKey.getString());

                // search for the expected values using SET_RANGE.
                byte[] keyBytes = testKey.getData();
                keyBytes[keyBytes.length - 1]--;
                status = cursor2.getSearchKeyRange
                    (testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals((String) entry.getValue(), testData.getString());
                assertEquals((String) entry.getKey(), testKey.getString());

                // check that getCurrent returns the same thing.
                status =
                    cursor2.getCurrent(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals((String) entry.getValue(), testData.getString());
                assertEquals((String) entry.getKey(), testKey.getString());
            }
        }
    }

    private void verifyDuplicates(Hashtable expectedData)
        throws DatabaseException {

        Enumeration iter = expectedData.keys();
        StringDbt testKey = new StringDbt();
        StringDbt testData = new StringDbt();

        // Iterate over the expected values.
        while (iter.hasMoreElements()) {
            String key = (String) iter.nextElement();
            testKey.setString(key);

            // search for the expected values using SET.
            OperationStatus status = cursor2.getSearchKey(testKey, testData,
                                                          LockMode.DEFAULT);
            assertEquals(OperationStatus.SUCCESS, status);
            assertEquals(key, testKey.getString());
            String dataString = testData.getString();

            // check that getCurrent returns the same thing.
            status = cursor2.getCurrent(testKey, testData, LockMode.DEFAULT);
            assertEquals(OperationStatus.SUCCESS, status);
            assertEquals(dataString, testData.getString());
            assertEquals(key, testKey.getString());

            // search for the expected values using SET_RANGE.
            byte[] keyBytes = testKey.getData();
            keyBytes[keyBytes.length - 1]--;
            status =
                cursor2.getSearchKeyRange(testKey, testData, LockMode.DEFAULT);
            assertEquals(OperationStatus.SUCCESS, status);
            assertEquals(dataString, testData.getString());
            assertEquals(key, testKey.getString());

            // check that getCurrent returns the same thing.
            status = cursor2.getCurrent(testKey, testData, LockMode.DEFAULT);
            assertEquals(OperationStatus.SUCCESS, status);
            assertEquals(dataString, testData.getString());
            assertEquals(key, testKey.getString());

            Hashtable ht = (Hashtable) expectedData.get(key);

            Enumeration iter2 = ht.keys();
            while (iter2.hasMoreElements()) {
                String expectedDataString = (String) iter2.nextElement();
                testData.setString(expectedDataString);

                // search for the expected values using SET_BOTH.
                status =
                    cursor2.getSearchBoth(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals(expectedDataString, testData.getString());
                assertEquals(key, testKey.getString());

                // check that getCurrent returns the same thing.
                status =
                    cursor2.getCurrent(testKey, testData, LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals(expectedDataString, testData.getString());
                assertEquals(key, testKey.getString());

                // search for the expected values using SET_RANGE_BOTH.
                byte[] dataBytes = testData.getData();
                dataBytes[dataBytes.length - 1]--;
                status = cursor2.getSearchBothRange(testKey, testData,
                                                    LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals(key, testKey.getString());
                assertEquals(expectedDataString, testData.getString());

                // check that getCurrent returns the same thing.
                status = cursor2.getCurrent(testKey, testData,
                                            LockMode.DEFAULT);
                assertEquals(OperationStatus.SUCCESS, status);
                assertEquals(expectedDataString, testData.getString());
                assertEquals(key, testKey.getString());
            }
        }
    }
}
