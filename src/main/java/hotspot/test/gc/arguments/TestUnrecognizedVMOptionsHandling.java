package hotspot.test.gc.arguments;/*
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
 * @test TestUnrecognizedVMOptionsHandling
 * @key gc
 * @bug 8017611
 * @summary Tests handling unrecognized VM options
 * @library /testlibrary
 * @run main/othervm TestUnrecognizedVMOptionsHandling
 */

import com.oracle.java.testlibrary.*;

public class TestUnrecognizedVMOptionsHandling {

  public static void main(String args[]) throws Exception {
    // The first two JAVA processes are expected to fail, but with a correct VM option suggestion
    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(
      "-XX:+PrintGc",
      "-version"
      );
    OutputAnalyzer outputWithError = new OutputAnalyzer(pb.start());
    outputWithError.shouldContain("Did you mean '(+/-)PrintGC'?");
    if (outputWithError.getExitValue() == 0) {
      throw new RuntimeException("Not expected to get exit value 0");
    }

    pb = ProcessTools.createJavaProcessBuilder(
      "-XX:MaxiumHeapSize=500m",
      "-version"
      );
    outputWithError = new OutputAnalyzer(pb.start());
    outputWithError.shouldContain("Did you mean 'MaxHeapSize=<value>'?");
    if (outputWithError.getExitValue() == 0) {
      throw new RuntimeException("Not expected to get exit value 0");
    }

    // The last JAVA process should run successfully for the purpose of sanity check
    pb = ProcessTools.createJavaProcessBuilder(
      "-XX:+PrintGC",
      "-version"
      );
    OutputAnalyzer outputWithNoError = new OutputAnalyzer(pb.start());
    outputWithNoError.shouldNotContain("Did you mean '(+/-)PrintGC'?");
    outputWithNoError.shouldHaveExitValue(0);
  }
}

