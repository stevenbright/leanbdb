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

package com.sleepycat.je.rep.elections;

import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;

public interface ElectionsConfig {

    /**
     * Gets the replication group name.
     * @return group name
     */
    public String getGroupName();

    /**
     * Gets the nodes NameIdPair.
     * @return NameIdPair
     */
    public NameIdPair getNameIdPair();

    /**
     * Gets the ServiceDispatcher.
     * @return ServiceDispatcher
     */
    public ServiceDispatcher getServiceDispatcher();

    /**
     * Gets the election priority.
     * @return election priority
     */
    public int getElectionPriority();

    /**
     * Gets the JE log version.
     * @return log version
     */
    public int getLogVersion();

    /**
     * Gets the RepImpl.
     * @return RepImpl
     */
    public RepImpl getRepImpl();

    /**
     * Get the RepNode. May be null if the Elections
     * object is not used for the initiation of
     * an election.
     * @return RepNode
     */
    public RepNode getRepNode();
}
