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

package com.sleepycat.je.rep.impl;

import java.util.concurrent.TimeUnit;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.ReplicaConsistencyPolicy;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.ReplicaConsistencyException;
import com.sleepycat.je.rep.impl.node.Replica;
import com.sleepycat.je.utilint.PropUtil;
import com.sleepycat.je.utilint.VLSN;

/**
 * This is used to ensure that the Replica has finished replaying or proceeded
 * past the vlsn specified by the policy. It's like the externally visible
 * CommitPointConsistencyPolicy, except that the latter restricts consistency
 * points to commit vlsns, whereas this policy lets you sync at uncommitted log
 * entries.
 */
public class PointConsistencyPolicy
    implements ReplicaConsistencyPolicy {

    /**
     * The name:{@value} associated with this policy. The name can be used when
     * constructing policy property values for use in je.properties files.
     */
    public static final String NAME = "PointConsistencyPolicy";

    private final VLSN targetVLSN;

    /*
     * Amount of time (in milliseconds) to wait for consistency to be
     * reached.
     */
    private final int timeout;

    public PointConsistencyPolicy(VLSN targetVLSN) {
        this(targetVLSN, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public PointConsistencyPolicy(VLSN targetVLSN,
                                  long timeout,
                                  TimeUnit timeoutUnit) {
        this.targetVLSN = targetVLSN;
        this.timeout = PropUtil.durationToMillis(timeout, timeoutUnit);
    }

    /**
     * Returns the name:{@value #NAME}, associated with this policy.
     * @see #NAME
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Ensures that the replica has replayed the replication stream to the
     * point identified by the commit token. If it isn't the method waits until
     * the constraint is satisfied by the replica.
     */
    @Override
    public void ensureConsistency(EnvironmentImpl replicatorImpl)
        throws InterruptedException,
               ReplicaConsistencyException,
               DatabaseException {

        /*
         * Cast is done to preserve replication/non replication code
         * boundaries.
         */
        RepImpl repImpl = (RepImpl) replicatorImpl;
        Replica replica = repImpl.getRepNode().replica();
        replica.getConsistencyTracker().awaitVLSN(targetVLSN.getSequence(),
                                                  this);
    }

    @Override
    public long getTimeout(TimeUnit unit) {
        return PropUtil.millisToDuration(timeout, unit);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((targetVLSN == null) ? 0 : targetVLSN.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PointConsistencyPolicy other = (PointConsistencyPolicy) obj;
        if (targetVLSN == null) {
            if (other.targetVLSN != null) {
                return false;
            }
        } else if (!targetVLSN.equals(other.targetVLSN)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getName() + " targetVLSN=" + targetVLSN;
    }
}
