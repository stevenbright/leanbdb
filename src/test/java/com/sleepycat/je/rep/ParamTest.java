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

package com.sleepycat.je.rep;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.util.test.SharedTestUtils;
import com.sleepycat.util.test.TestBase;

/**
 * Test setting and retrieving of replication configurations. Should test
 * - mutable configurations
 * - the static fields in ReplicatorParams most be loaded properly.
 *
 * TBW - test is incomplete.

 * Test setting and retrieving of replication params. Make sure we can
 * parse the special format of the je.rep.node.* param, and that we
 * give params specified in files precedence over params specified
 * programmatically.
 */
public class ParamTest extends TestBase {

    private final File envRoot;

    public ParamTest() {
        envRoot = SharedTestUtils.getTestDir();
    }

    /**
     * THIS TESTCASE should go first in this file, before a replicator is
     * instantiated in this JVM, to ensure that an application can instantiate
     * a ReplicationConfig before instantiating a replicated environment.
     * ReplicationConfig needs a static from ReplicatorParams, and we have to
     * make sure it is loaded properly.
     */
    @Test
    public void testConfigSetting() {
        ReplicationConfig repConfig = new ReplicationConfig();
        repConfig.setConfigParam("je.rep.groupName", "TestGroup");
    }

    /**
     * Make sure that this valid property can be set through both a file and
     * through a configuration instance.
     */
    private void verifySuccess(String paramName, String value) {
        try {
            Properties props = new Properties();
            props.put(paramName, value);
            DbConfigManager.validateProperties(props, false, null);
        } catch (Exception E) {
            E.printStackTrace();
            fail("Unexpected exception: " + E);
        }

        try {
            ReplicationConfig goodConfig = new ReplicationConfig();
            goodConfig.setConfigParam(paramName, value);
        } catch (Exception E) {
            E.printStackTrace();
            fail("Unexpected exception: " + E);
    }

        try {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setConfigParam(paramName, value);
            fail(paramName + " should have been rejected");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Make sure that this invalid property will be caught when set through
     * either a file, or a configuration instance.
     */
    void verifyFailure(String paramName, String badValue) {
        try {
            Properties props = new Properties();
            props.put(paramName, badValue);
            DbConfigManager.validateProperties(props, false, null);
            fail("Bad value: " + badValue+ " not detected.");
        } catch (IllegalArgumentException expected) {
        }

        try {
            ReplicationConfig badConfig = new ReplicationConfig();
            badConfig.setConfigParam(paramName, badValue);
            fail("Bad value: " + badValue+ " not detected.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGroupName() {
        verifySuccess(RepParams.GROUP_NAME.getName(), "SleepycatGroup1");
        verifyFailure(RepParams.GROUP_NAME.getName(),
                      "Sleepycat Group 1");
    }

    @Test
    public void testNodeType() {
        verifySuccess(RepParams.NODE_TYPE.getName(), "ELECTABLE");
        verifySuccess(RepParams.NODE_TYPE.getName(), "MONITOR");
        verifySuccess(RepParams.NODE_TYPE.getName(), "SECONDARY");
        verifyFailure(RepParams.NODE_TYPE.getName(), "NOT-A-NODE-TYPE");
    }
}
