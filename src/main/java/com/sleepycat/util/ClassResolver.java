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

package com.sleepycat.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Implements policies for loading user-supplied classes.  The {@link
 * #resolveClass} method should be used to load all user-supplied classes, and
 * the {@link Stream} class should be used as a replacement for
 * ObjectInputStream to deserialize instances of user-supplied classes.
 * <p>
 * The ClassLoader specified as a param should be the one configured using
 * EnvironmentConfig.setClassLoader.  This loader is used, if non-null.  If the
 * loader param is null, but a non-null thread-context loader is available, the
 * latter is used.  If the loader param and thread-context loader are both
 * null, or if they fail to load a class by throwing ClassNotFoundException,
 * then the default Java mechanisms for determining the class loader are used.
 */
public class ClassResolver {
    
    /**
     * A specialized ObjectInputStream that supports use of a user-specified
     * ClassLoader.
     *
     * If the loader param and thread-context loader are both null, of if they
     * throw ClassNotFoundException, then ObjectInputStream.resolveClass is
     * called, which has its own special rules for class loading.
     */
    public static class Stream extends ObjectInputStream {

        private final ClassLoader classLoader;

        public Stream(InputStream in, ClassLoader classLoader)
            throws IOException {

            super(in);
            this.classLoader = classLoader;
        }

        @Override
        protected Class resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {

            ClassNotFoundException firstException = null;
            if (classLoader != null) {
                try {
                    return Class.forName(desc.getName(), false /*initialize*/,
                                         classLoader);
                } catch (ClassNotFoundException e) {
                    if (firstException == null) {
                        firstException = e;
                    }
                }
            }
            final ClassLoader threadLoader = 
                Thread.currentThread().getContextClassLoader();
            if (threadLoader != null) {
                try {
                    return Class.forName(desc.getName(), false /*initialize*/,
                                         threadLoader);
                } catch (ClassNotFoundException e) {
                    if (firstException == null) {
                        firstException = e;
                    }
                }
            }
            try {
                return super.resolveClass(desc);
            } catch (ClassNotFoundException e) {
                if (firstException == null) {
                    firstException = e;
                }
            }
            throw firstException;
        }
    }

    /**
     * A specialized Class.forName method that supports use of a user-specified
     * ClassLoader.
     *
     * If the loader param and thread-context loader are both null, of if they
     * throw ClassNotFoundException, then Class.forName is called and the
     * "current loader" (the one used to load JE) will be used.
     *
     * @param className the class name.
     * @param classLoader the ClassLoader.
     * @return the Class.
     * @throws ClassNotFoundException if the class is not found.
     */
    public static Class resolveClass(String className,
                                     ClassLoader classLoader)
        throws ClassNotFoundException {

        ClassNotFoundException firstException = null;
        if (classLoader != null) {
            try {
                return Class.forName(className, true /*initialize*/,
                                     classLoader);
            } catch (ClassNotFoundException e) {
                if (firstException == null) {
                    firstException = e;
                }
            }
        }
        final ClassLoader threadLoader = 
            Thread.currentThread().getContextClassLoader();
        if (threadLoader != null) {
            try {
                return Class.forName(className, true /*initialize*/,
                                     threadLoader);
            } catch (ClassNotFoundException e) {
                if (firstException == null) {
                    firstException = e;
                }
            }
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            if (firstException == null) {
                firstException = e;
            }
        }
        throw firstException;
    }
}
