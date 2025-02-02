package hotspot.test.runtime.memory;/*
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
 * @key regression
 * @bug 8012015
 * @summary Make sure reserved (but uncommitted) memory is not accessible
 * @library /testlibrary /testlibrary/whitebox
 * @build ReserveMemory
 * @run main ClassFileInstaller sun.hotspot.WhiteBox
 * @run main ReserveMemory
 */

import com.oracle.java.testlibrary.*;

import sun.hotspot.WhiteBox;

public class ReserveMemory {
  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().startsWith("win");
  }

  private static boolean isOsx() {
    return System.getProperty("os.name").toLowerCase().startsWith("mac");
  }

  public static void main(String args[]) throws Exception {
    if (args.length > 0) {
      WhiteBox.getWhiteBox().readReservedMemory();

      throw new Exception("Read of reserved/uncommitted memory unexpectedly succeeded, expected crash!");
    }

    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(
          "-Xbootclasspath/a:.",
          "-XX:+UnlockDiagnosticVMOptions",
          "-XX:+WhiteBoxAPI",
          "-XX:-TransmitErrorReport",
          "ReserveMemory",
          "test");

    OutputAnalyzer output = new OutputAnalyzer(pb.start());
    if (isWindows()) {
      output.shouldContain("EXCEPTION_ACCESS_VIOLATION");
    } else if (isOsx()) {
      output.shouldContain("SIGBUS");
    } else {
      output.shouldContain("SIGSEGV");
    }
  }
}
