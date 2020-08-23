package hotspot.test.runtime.CommandLine;/*
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
 * @test ConfigFileParsing
 * @bug 7158804
 * @summary Improve config file parsing
 * @library /testlibrary
 */

import java.io.PrintWriter;
import com.oracle.java.testlibrary.*;

public class ConfigFileParsing {
  public static void main(String[] args) throws Exception {
    String testFileName = ".hotspotrc";

    // Create really long invalid option
    String reallyLongInvalidOption = "";
    for (int i=0; i<5000; i++)
      reallyLongInvalidOption+='a';

    // Populate the options file with really long string
    PrintWriter pw = new PrintWriter(testFileName);
    pw.println("-XX:+" + reallyLongInvalidOption);
    pw.close();

    // start VM
    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(
        "-XX:+IgnoreUnrecognizedVMOptions", "-XX:Flags=.hotspotrc", "-version");

    OutputAnalyzer output = new OutputAnalyzer(pb.start());
    output.shouldHaveExitValue(0);
  }
}
