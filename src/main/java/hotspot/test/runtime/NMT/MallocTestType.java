package hotspot.test.runtime.NMT;/*
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
 * @summary Test consistency of NMT by leaking a few select allocations of the Test type and then verify visibility with jcmd
 * @key nmt jcmd
 * @library /testlibrary /testlibrary/whitebox
 * @build MallocTestType
 * @run main ClassFileInstaller sun.hotspot.WhiteBox
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -XX:NativeMemoryTracking=detail MallocTestType
 */

import com.oracle.java.testlibrary.*;
import sun.hotspot.WhiteBox;

public class MallocTestType {

  public static void main(String args[]) throws Exception {
    OutputAnalyzer output;
    WhiteBox wb = WhiteBox.getWhiteBox();

    // Grab my own PID
    String pid = Integer.toString(ProcessTools.getProcessId());
    ProcessBuilder pb = new ProcessBuilder();

    // Use WB API to alloc and free with the mtTest type
    long memAlloc3 = wb.NMTMalloc(128 * 1024);
    long memAlloc2 = wb.NMTMalloc(256 * 1024);
    wb.NMTFree(memAlloc3);
    long memAlloc1 = wb.NMTMalloc(512 * 1024);
    wb.NMTFree(memAlloc2);

    // Use WB API to ensure that all data has been merged before we continue
    if (!wb.NMTWaitForDataMerge()) {
      throw new Exception("Call to WB API NMTWaitForDataMerge() failed");
    }

    // Run 'jcmd <pid> VM.native_memory summary'
    pb.command(new String[] { JDKToolFinder.getJDKTool("jcmd"), pid, "VM.native_memory", "summary"});
    output = new OutputAnalyzer(pb.start());
    output.shouldContain("Test (reserved=512KB, committed=512KB)");

    // Free the memory allocated by NMTAllocTest
    wb.NMTFree(memAlloc1);

    // Use WB API to ensure that all data has been merged before we continue
    if (!wb.NMTWaitForDataMerge()) {
      throw new Exception("Call to WB API NMTWaitForDataMerge() failed");
    }
    output = new OutputAnalyzer(pb.start());
    output.shouldNotContain("Test (reserved=");
  }
}
