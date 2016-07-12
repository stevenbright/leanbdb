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

package com.sleepycat.util.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/** A JUnit matcher that matches a number greater than the specified value. */
public class GreaterThan extends BaseMatcher<Number> {

    private final Number value;

    /**
     * Returns a matcher that checks for a number greater than the specified
     * value.
     *
     * @param value the value to check against
     * @return the matcher
     */
    public static Matcher greaterThan(Number value) {
        return new GreaterThan(value);
    }

    /**
     * Creates a matcher that checks for a number greater than the specified
     * value.
     *
     * @param value the value to check against
     */
    public GreaterThan(Number value) {
        this.value = value;
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof Number)) {
            return false;
        }
        if ((item instanceof Double) || (item instanceof Float)) {
            final double d = ((Number) item).doubleValue();
            return d > value.doubleValue();
        } else {
            final long l = ((Number) item).longValue();
            return l > value.longValue();
        }
    }

    @Override
    public void describeTo(Description desc) {
        desc.appendText(" number greater than ").appendValue(value);
    }
}
