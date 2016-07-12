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

package com.sleepycat.je.rep.utilint.net;

import com.sleepycat.je.rep.net.DataChannel;
import java.nio.channels.SocketChannel;

/**
 * An abstract class that utilizes a delegate socketChannel for network
 * I/O, but which provides an abstract ByteChannel interface for callers.
 * This allows more interesting communication mechanisms to be introduced.
 */
abstract public class AbstractDataChannel implements DataChannel {

    /**
     * The underlying socket channel
     */
    protected final SocketChannel socketChannel;

    /**
     * Constructor for sub-classes.
     * @param socketChannel The underlying SocketChannel over which data will
     *        be sent.  This should be the lowest-level socket so that select
     *        operations can be performed on it.
     */
    protected AbstractDataChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * Accessor for the underlying SocketChannel
     * Callers may used the returned SocketChannel in order to query/modify
     * connections attributes, but may not directly close, read from or write
     * to the SocketChannel.
     */
    @Override
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}

