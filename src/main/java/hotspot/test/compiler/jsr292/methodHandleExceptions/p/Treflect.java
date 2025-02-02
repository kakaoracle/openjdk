/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */
package hotspot.test.compiler.jsr292.methodHandleExceptions.p;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invokes I.m using reflection.
 */
public class Treflect {

    public static int test(p.I ii) throws Throwable {
        int accum = 0;
        Method m = p.I.class.getMethod("m");
        try {
            for (int j = 0; j < 100000; j++) {
                Object o = m.invoke(ii);
                accum += ((Integer) o).intValue();
            }
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
        return accum;
    }

    public static int test(p.I ii, byte b, char c, short s, int i, long l,
            Object o1, Object o2, Object o3, Object o4, Object o5, Object o6)
            throws Throwable {
        Method m = p.I.class.getMethod("m", Byte.TYPE, Character.TYPE,
                Short.TYPE, Integer.TYPE, Long.TYPE,
                Object.class, Object.class, Object.class,
                Object.class, Object.class, Object.class);
        int accum = 0;
        try {
            for (int j = 0; j < 100000; j++) {
                Object o = m.invoke(ii, b, c, s, i, l, o1, o2, o3, o4, o5, o6);
                accum += ((Integer) o).intValue();
            }
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
        return accum;
    }
}
