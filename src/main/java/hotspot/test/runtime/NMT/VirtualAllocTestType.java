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
 * @summary Test Reserve/Commit/Uncommit/Release of virtual memory and that we track it correctly
 * @key nmt jcmd
 * @library /testlibrary /testlibrary/whitebox
 * @build VirtualAllocTestType
 * @run main ClassFileInstaller sun.hotspot.WhiteBox
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -XX:NativeMemoryTracking=detail VirtualAllocTestType
 */

import com.oracle.java.testlibrary.*;
import sun.hotspot.WhiteBox;

public class VirtualAllocTestType {

  public static WhiteBox wb = WhiteBox.getWhiteBox();
  public static void main(String args[]) throws Exception {
    OutputAnalyzer output;
    long commitSize = 128 * 1024;
    long reserveSize = 256 * 1024;
    long addr;

    String pid = Integer.toString(ProcessTools.getProcessId());
    ProcessBuilder pb = new ProcessBuilder();

    boolean has_nmt_detail = wb.NMTIsDetailSupported();
    if (has_nmt_detail) {
      System.out.println("NMT detail support detected.");
    } else {
      System.out.println("NMT detail support not detected.");
    }

    addr = wb.NMTReserveMemory(reserveSize);
    mergeData();
    pb.command(new String[] { JDKToolFinder.getJDKTool("jcmd"), pid, "VM.native_memory", "detail"});

    output = new OutputAnalyzer(pb.start());
    output.shouldContain("Test (reserved=256KB, committed=0KB)");
    if (has_nmt_detail) {
      output.shouldMatch("\\[0x[0]*" + Long.toHexString(addr) + " - 0x[0]*" + Long.toHexString(addr + reserveSize) + "\\] reserved 256KB for Test");
    }

    wb.NMTCommitMemory(addr, commitSize);

    mergeData();

    output = new OutputAnalyzer(pb.start());
    output.shouldContain("Test (reserved=256KB, committed=128KB)");
    if (has_nmt_detail) {
      output.shouldMatch("\\[0x[0]*" + Long.toHexString(addr) + " - 0x[0]*" + Long.toHexString(addr + commitSize) + "\\] committed 128KB");
    }

    wb.NMTUncommitMemory(addr, commitSize);

    mergeData();

    output = new OutputAnalyzer(pb.start());
    output.shouldContain("Test (reserved=256KB, committed=0KB)");
    output.shouldNotMatch("\\[0x[0]*" + Long.toHexString(addr) + " - 0x[0]*" + Long.toHexString(addr + commitSize) + "\\] committed");

    wb.NMTReleaseMemory(addr, reserveSize);
    mergeData();

    output = new OutputAnalyzer(pb.start());
    output.shouldNotContain("Test (reserved=");
    output.shouldNotMatch("\\[0x[0]*" + Long.toHexString(addr) + " - 0x[0]*" + Long.toHexString(addr + reserveSize) + "\\] reserved");
  }

  public static void mergeData() throws Exception {
    // Use WB API to ensure that all data has been merged before we continue
    if (!wb.NMTWaitForDataMerge()) {
      throw new Exception("Call to WB API NMTWaitForDataMerge() failed");
    }
  }
}
