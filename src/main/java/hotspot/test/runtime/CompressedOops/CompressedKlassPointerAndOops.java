package hotspot.test.runtime.CompressedOops;/*
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
 */

/*
 * @test
 * @bug 8000968
 * @key regression
 * @summary NPG: UseCompressedClassPointers asserts with ObjectAlignmentInBytes=32
 * @library /testlibrary
 */

import com.oracle.java.testlibrary.*;

public class CompressedKlassPointerAndOops {

    public static void main(String[] args) throws Exception {

        if (!Platform.is64bit()) {
            // Can't test this on 32 bit, just pass
            System.out.println("Skipping test on 32bit");
            return;
        }

        runWithAlignment(16);
        runWithAlignment(32);
        runWithAlignment(64);
        runWithAlignment(128);
    }

    private static void runWithAlignment(int alignment) throws Exception {
        ProcessBuilder pb;
        OutputAnalyzer output;

        pb = ProcessTools.createJavaProcessBuilder(
            "-XX:+UseCompressedClassPointers",
            "-XX:+UseCompressedOops",
            "-XX:ObjectAlignmentInBytes=" + alignment,
            "-version");

        output = new OutputAnalyzer(pb.start());
        output.shouldHaveExitValue(0);
    }
}
