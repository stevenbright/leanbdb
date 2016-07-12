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

public enum SearchMode {
    SET(true, false, "SET"),
    BOTH(true, true, "BOTH"),
    SET_RANGE(false, false, "SET_RANGE"),
    BOTH_RANGE(false, true, "BOTH_RANGE");

    private final boolean exactSearch;
    private final boolean dataSearch;
    private final String name;

    private SearchMode(boolean exactSearch,
                       boolean dataSearch,
                       String name) {
        this.exactSearch = exactSearch;
        this.dataSearch = dataSearch;
        this.name = "SearchMode." + name;
    }

    /**
     * Returns true when the key or key/data search is exact, i.e., for SET
     * and BOTH.
     */
    public final boolean isExactSearch() {
        return exactSearch;
    }

    /**
     * Returns true when the data value is included in the search, i.e., for
     * BOTH and BOTH_RANGE.
     */
    public final boolean isDataSearch() {
        return dataSearch;
    }

    @Override
    public String toString() {
        return name;
    }
}
