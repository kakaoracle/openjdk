package hotspot.test.compiler/*
 * Copyright (c) 2009, 2010, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test
 * @bug 6896727
 * @summary nsk/logging/LoggingPermission/LoggingPermission/logperm002 fails with G1, EscapeAnalisys w/o COOPs
 * @run main/othervm -XX:+IgnoreUnrecognizedVMOptions -Xcomp -XX:+DoEscapeAnalysis Test
 */

public class Test {

    final static String testString = "abracadabra";
    public static void main(String args[]) {
        String params[][] = {
            {"control", testString}
        };
        for (int i=0; i<params.length; i++) {
            try {
                System.out.println("Params :" + testString + " and " + params[i][0] + ", " + params[i][1]);
                if (params[i][1] == null) {
                    System.exit(97);
                }
            } catch (Exception e) {}
        }
    }
}
