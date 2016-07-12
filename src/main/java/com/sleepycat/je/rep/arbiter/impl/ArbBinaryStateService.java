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

package com.sleepycat.je.rep.arbiter.impl;

import java.io.IOException;
import java.util.logging.Logger;

import com.sleepycat.je.JEVersion;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.rep.impl.BinaryNodeStateProtocol;
import com.sleepycat.je.rep.impl.BinaryNodeStateProtocol.BinaryNodeStateRequest;
import com.sleepycat.je.rep.impl.BinaryNodeStateProtocol.BinaryNodeStateResponse;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.BinaryProtocol.ProtocolException;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingService;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.JVMSystemUtils;

/**
 * The service registered by an Arbiter to answer the state request.
 *
 * To support the new BinaryStateProtocol, we introduce this new
 * BinaryNodeStateService, it's used by "Ping" command.
 *
 */
public class ArbBinaryStateService extends ExecutingService {

    private final ArbiterImpl arbImpl;
    private final ServiceDispatcher dispatcher;
    private final Logger logger;

    /* Identifies the Node State querying Service. */
    public static final String SERVICE_NAME = "BinaryNodeState";

    public ArbBinaryStateService(ServiceDispatcher dispatcher,
                                 ArbiterImpl arbImpl) {
        super(SERVICE_NAME, dispatcher);
        this.arbImpl = arbImpl;;
        this.dispatcher = dispatcher;
        this.logger = LoggerUtils.getLogger(getClass());

        dispatcher.register(this);
    }

    public void shutdown() {
        dispatcher.cancel(SERVICE_NAME);
    }

    @Override
    public Runnable getRunnable(DataChannel dataChannel) {
        return new NodeStateServiceRunnable(dataChannel);
    }

    class NodeStateServiceRunnable implements Runnable {
        private final DataChannel channel;

        NodeStateServiceRunnable(DataChannel channel) {
            this.channel = channel;
        }

        /* Create the NodeState for the request. */
        private BinaryNodeStateResponse createResponse
            (BinaryNodeStateProtocol protocol) {

            long joinTime = arbImpl.getJoinGroupTime();
            long vlsnValue = (arbImpl.getArbiterVLSNTracker().get() == null ?
                    0L : arbImpl.getArbiterVLSNTracker().get().getSequence());

            return protocol.new BinaryNodeStateResponse(
                arbImpl.getNameIdPair().getName(),
                arbImpl.getGroupName(),
                arbImpl.getMasterStatus().getNodeMasterNameId().getName(),
                JEVersion.CURRENT_VERSION, joinTime,
                arbImpl.getNodeState(),
                vlsnValue, vlsnValue,
                0, LogEntryType.LOG_VERSION,
                null, JVMSystemUtils.getSystemLoad());
        }

        @Override
        public void run() {
            BinaryNodeStateProtocol protocol = null;

            try {
                protocol = new BinaryNodeStateProtocol(NameIdPair.NOCHECK,
                                                       arbImpl.getRepImpl());
                try {
                    channel.getSocketChannel().configureBlocking(true);

                    BinaryNodeStateRequest msg =
                        protocol.read(channel, BinaryNodeStateRequest.class);

                    /*
                     * Response a protocol error if the group name doesn't
                     * match.
                     */
                    final String groupName = msg.getGroupName();
                    if (!arbImpl.getGroupName().equals(groupName) ||
                        !arbImpl.getNameIdPair().getName().
                            equals(msg.getNodeName())) {
                        throw new ProtocolException("Sending the request to" +
                                " a wrong group or a wrong node.");
                    }

                    /* Write the response the requested node. */
                    BinaryNodeStateResponse response =
                        createResponse(protocol);
                    protocol.write(response, channel);
                    LoggerUtils.finest(logger, arbImpl.getRepImpl(),
                            "Deal with a node state request successfully.");
                } catch (ProtocolException e) {
                    LoggerUtils.info(logger, arbImpl.getRepImpl(),
                            "Get a ProtocolException with message: " +
                            LoggerUtils.exceptionTypeAndMsg(e) +
                            " while dealing with a node state request.");
                    protocol.write
                        (protocol.new ProtocolError(e.getMessage()), channel);
                } catch (Exception e) {
                    LoggerUtils.info(logger, arbImpl.getRepImpl(),
                            "Unexpected exception: " +
                             LoggerUtils.exceptionTypeAndMsg(e));
                    protocol.write
                        (protocol.new ProtocolError(e.getMessage()), channel);
                } finally {
                    if (channel.isOpen()) {
                        channel.close();
                    }
                }
            } catch (IOException e) {

                /*
                 * Channel has already been closed, or the close itself
                 * failed.
                 */
            }
        }
    }
}
