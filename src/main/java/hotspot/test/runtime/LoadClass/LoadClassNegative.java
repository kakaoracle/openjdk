package hotspot.test.runtime.LoadClass;/*
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
 * @ignore 8028095
 * @test
 * @key regression
 * @bug 8020675
 * @summary make sure there is no fatal error if a class is loaded from an invalid jar file which is in the bootclasspath
 * @library /testlibrary
 * @build TestForName
 * @build LoadClassNegative
 * @run main LoadClassNegative
 */

import java.io.File;
import com.oracle.java.testlibrary.*;

public class LoadClassNegative {

  public static void main(String args[]) throws Exception {
    String bootCP = "-Xbootclasspath/a:" + System.getProperty("test.src")
                       + File.separator + "dummy.jar";
    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(
        bootCP,
        "TestForName");

    OutputAnalyzer output = new OutputAnalyzer(pb.start());
    output.shouldContain("ClassNotFoundException");
    output.shouldHaveExitValue(0);
  }
}
