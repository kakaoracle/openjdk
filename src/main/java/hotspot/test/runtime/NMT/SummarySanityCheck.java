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
 * @key nmt jcmd
 * @summary Sanity check the output of NMT
 * @library /testlibrary /testlibrary/whitebox
 * @build SummarySanityCheck
 * @run main ClassFileInstaller sun.hotspot.WhiteBox
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+WhiteBoxAPI SummarySanityCheck
 */

import com.oracle.java.testlibrary.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.hotspot.WhiteBox;

public class SummarySanityCheck {

  private static String jcmdout;
  public static void main(String args[]) throws Exception {
    // Grab my own PID
    String pid = Integer.toString(ProcessTools.getProcessId());

    // Use WB API to ensure that all data has been merged before we continue
    if (!WhiteBox.getWhiteBox().NMTWaitForDataMerge()) {
      throw new Exception("Call to WB API NMTWaitForDataMerge() failed");
    }

    ProcessBuilder pb = new ProcessBuilder();

    // Run  'jcmd <pid> VM.native_memory summary scale=KB'
    pb.command(new String[] { JDKToolFinder.getJDKTool("jcmd"), pid, "VM.native_memory", "summary", "scale=KB"});
    OutputAnalyzer output = new OutputAnalyzer(pb.start());

    jcmdout = output.getOutput();
    // Split by '-' to get the 'groups'
    String[] lines = jcmdout.split("\n");

    if (lines.length == 0) {
      throwTestException("Failed to parse jcmd output");
    }

    int totalCommitted = 0, totalReserved = 0;
    int totalCommittedSum = 0, totalReservedSum = 0;

    // Match '- <mtType> (reserved=<reserved>KB, committed=<committed>KB)
    Pattern mtTypePattern = Pattern.compile("-\\s+(?<typename>[\\w\\s]+)\\(reserved=(?<reserved>\\d+)KB,\\scommitted=(?<committed>\\d+)KB\\)");
    // Match 'Total: reserved=<reserved>KB, committed=<committed>KB'
    Pattern totalMemoryPattern = Pattern.compile("Total\\:\\s\\sreserved=(?<reserved>\\d+)KB,\\s\\scommitted=(?<committed>\\d+)KB");

    for (int i = 0; i < lines.length; i++) {
      if (lines[i].startsWith("Total")) {
        Matcher totalMemoryMatcher = totalMemoryPattern.matcher(lines[i]);

        if (totalMemoryMatcher.matches() && totalMemoryMatcher.groupCount() == 2) {
          totalCommitted = Integer.parseInt(totalMemoryMatcher.group("committed"));
          totalReserved = Integer.parseInt(totalMemoryMatcher.group("reserved"));
        } else {
          throwTestException("Failed to match the expected groups in 'Total' memory part");
        }
      } else if (lines[i].startsWith("-")) {
        Matcher typeMatcher = mtTypePattern.matcher(lines[i]);
        if (typeMatcher.matches()) {
          int typeCommitted = Integer.parseInt(typeMatcher.group("committed"));
          int typeReserved = Integer.parseInt(typeMatcher.group("reserved"));

          // Make sure reserved is always less or equals
          if (typeCommitted > typeReserved) {
            throwTestException("Committed (" + typeCommitted + ") was more than Reserved ("
                + typeReserved + ") for mtType: " + typeMatcher.group("typename"));
          }

          // Add to total and compare them in the end
          totalCommittedSum += typeCommitted;
          totalReservedSum += typeReserved;
        } else {
          throwTestException("Failed to match the group on line " + i);
        }
      }
    }

    // See if they add up correctly, rounding is a problem so make sure we're within +/- 8KB
    int committedDiff = totalCommitted - totalCommittedSum;
    if (committedDiff > 8 || committedDiff < -8) {
      throwTestException("Total committed (" + totalCommitted + ") did not match the summarized committed (" + totalCommittedSum + ")" );
    }

    int reservedDiff = totalReserved - totalReservedSum;
    if (reservedDiff > 8 || reservedDiff < -8) {
      throwTestException("Total reserved (" + totalReserved + ") did not match the summarized reserved (" + totalReservedSum + ")" );
    }
  }

  private static void throwTestException(String reason) throws Exception {
      throw new Exception(reason + " . Stdout is :\n" + jcmdout);
  }
}
