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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;

import com.oracle.java.testlibrary.*;
import sun.hotspot.WhiteBox;

class ErgoArgsPrinter {
  public static void main(String[] args) throws Exception {
    WhiteBox wb = WhiteBox.getWhiteBox();
    wb.printHeapSizes();
  }
}

final class MinInitialMaxValues {
  public long minHeapSize;
  public long initialHeapSize;
  public long maxHeapSize;

  public long minAlignment;
  public long maxAlignment;
}

class TestMaxHeapSizeTools {

  public static void checkMinInitialMaxHeapFlags(String gcflag) throws Exception {
    checkInvalidMinInitialHeapCombinations(gcflag);
    checkValidMinInitialHeapCombinations(gcflag);
    checkInvalidInitialMaxHeapCombinations(gcflag);
    checkValidInitialMaxHeapCombinations(gcflag);
  }

  public static void checkMinInitialErgonomics(String gcflag) throws Exception {
    // heap sizing ergonomics use the value NewSize + OldSize as default values
    // for ergonomics calculation. Retrieve these values.
    long[] values = new long[2];
    getNewOldSize(gcflag, values);

    // we check cases with values smaller and larger than this default value.
    long newPlusOldSize = values[0] + values[1];
    long smallValue = newPlusOldSize / 2;
    long largeValue = newPlusOldSize * 2;
    long maxHeapSize = largeValue + (2 * 1024 * 1024);

    // -Xms is not set
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize }, values, -1, -1);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-XX:InitialHeapSize=" + smallValue }, values, -1, smallValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-XX:InitialHeapSize=" + largeValue }, values, -1, largeValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-XX:InitialHeapSize=0" }, values, -1, -1);

    // -Xms is set to zero
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms0" }, values, -1, -1);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms0", "-XX:InitialHeapSize=" + smallValue }, values, -1, smallValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms0", "-XX:InitialHeapSize=" + largeValue }, values, -1, largeValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms0", "-XX:InitialHeapSize=0" }, values, -1, -1);

    // -Xms is set to small value
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms" + smallValue }, values, -1, -1);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms" + smallValue, "-XX:InitialHeapSize=" + smallValue }, values, smallValue, smallValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms" + smallValue, "-XX:InitialHeapSize=" + largeValue }, values, smallValue, largeValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms" + smallValue, "-XX:InitialHeapSize=0" }, values, smallValue, -1);

    // -Xms is set to large value
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms" + largeValue }, values, largeValue, largeValue);
    checkErgonomics(new String[] { gcflag, "-Xmx" + maxHeapSize, "-Xms" + largeValue, "-XX:InitialHeapSize=0" }, values, largeValue, -1);
  }

  private static long align_up(long value, long alignment) {
    long alignmentMinusOne = alignment - 1;
    return (value + alignmentMinusOne) & ~alignmentMinusOne;
  }

  private static void getNewOldSize(String gcflag, long[] values) throws Exception {
    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(gcflag,
      "-XX:+PrintFlagsFinal", "-version");
    OutputAnalyzer output = new OutputAnalyzer(pb.start());
    output.shouldHaveExitValue(0);

    String stdout = output.getStdout();
    values[0] = getFlagValue(" NewSize", stdout);
    values[1] = getFlagValue(" OldSize", stdout);
  }

  public static void checkGenMaxHeapErgo(String gcflag) throws Exception {
    TestMaxHeapSizeTools.checkGenMaxHeapSize(gcflag, 3);
    TestMaxHeapSizeTools.checkGenMaxHeapSize(gcflag, 4);
    TestMaxHeapSizeTools.checkGenMaxHeapSize(gcflag, 5);
  }

  private static void checkInvalidMinInitialHeapCombinations(String gcflag) throws Exception {
    expectError(new String[] { gcflag, "-Xms8M", "-XX:InitialHeapSize=4M", "-version" });
  }

  private static void checkValidMinInitialHeapCombinations(String gcflag) throws Exception {
    expectValid(new String[] { gcflag, "-XX:InitialHeapSize=8M", "-Xms4M", "-version" });
    expectValid(new String[] { gcflag, "-Xms4M", "-XX:InitialHeapSize=8M", "-version" });
    expectValid(new String[] { gcflag, "-XX:InitialHeapSize=8M", "-Xms8M", "-version" });
    // the following is not an error as -Xms sets both minimal and initial heap size
    expectValid(new String[] { gcflag, "-XX:InitialHeapSize=4M", "-Xms8M", "-version" });
  }

  private static void checkInvalidInitialMaxHeapCombinations(String gcflag) throws Exception {
    expectError(new String[] { gcflag, "-XX:MaxHeapSize=4M", "-XX:InitialHeapSize=8M", "-version" });
    expectError(new String[] { gcflag, "-XX:InitialHeapSize=8M", "-XX:MaxHeapSize=4M", "-version" });
  }

  private static void checkValidInitialMaxHeapCombinations(String gcflag) throws Exception {
    expectValid(new String[] { gcflag, "-XX:InitialHeapSize=4M", "-XX:MaxHeapSize=8M", "-version" });
    expectValid(new String[] { gcflag, "-XX:MaxHeapSize=8M", "-XX:InitialHeapSize=4M", "-version" });
    expectValid(new String[] { gcflag, "-XX:MaxHeapSize=4M", "-XX:InitialHeapSize=4M", "-version" });
    // a value of "0" for initial heap size means auto-detect
    expectValid(new String[] { gcflag, "-XX:MaxHeapSize=4M", "-XX:InitialHeapSize=0M", "-version" });
  }

  private static long valueAfter(String source, String match) {
    int start = source.indexOf(match) + match.length();
    String tail = source.substring(start).split(" ")[0];
    return Long.parseLong(tail);
  }

  /**
   * Executes a new VM process with the given class and parameters.
   * @param vmargs Arguments to the VM to run
   * @param classname Name of the class to run
   * @param arguments Arguments to the class
   * @param useTestDotJavaDotOpts Use test.java.opts as part of the VM argument string
   * @return The OutputAnalyzer with the results for the invocation.
   */
  public static OutputAnalyzer runWhiteBoxTest(String[] vmargs, String classname, String[] arguments, boolean useTestDotJavaDotOpts) throws Exception {
    ArrayList<String> finalargs = new ArrayList<String>();

    String[] whiteboxOpts = new String[] {
      "-Xbootclasspath/a:.",
      "-XX:+UnlockDiagnosticVMOptions", "-XX:+WhiteBoxAPI",
      "-cp", System.getProperty("java.class.path"),
    };

    if (useTestDotJavaDotOpts) {
      // System.getProperty("test.java.opts") is '' if no options is set,
      // we need to skip such a result
      String[] externalVMOpts = new String[0];
      if (System.getProperty("test.java.opts") != null && System.getProperty("test.java.opts").length() != 0) {
        externalVMOpts = System.getProperty("test.java.opts").split(" ");
      }
      finalargs.addAll(Arrays.asList(externalVMOpts));
    }

    finalargs.addAll(Arrays.asList(vmargs));
    finalargs.addAll(Arrays.asList(whiteboxOpts));
    finalargs.add(classname);
    finalargs.addAll(Arrays.asList(arguments));

    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(finalargs.toArray(new String[0]));
    OutputAnalyzer output = new OutputAnalyzer(pb.start());
    output.shouldHaveExitValue(0);

    return output;
  }

  private static void getMinInitialMaxHeap(String[] args, MinInitialMaxValues val) throws Exception {
    OutputAnalyzer output = runWhiteBoxTest(args, ErgoArgsPrinter.class.getName(), new String[] {}, false);

    // the output we watch for has the following format:
    //
    // "Minimum heap X Initial heap Y Maximum heap Z Min alignment A Max Alignment B"
    //
    // where A, B, X, Y and Z are sizes in bytes.
    // Unfortunately there is no other way to retrieve the minimum heap size and
    // the alignments.

    Matcher m = Pattern.compile("Minimum heap \\d+ Initial heap \\d+ Maximum heap \\d+ Min alignment \\d+ Max alignment \\d+").
      matcher(output.getStdout());
    if (!m.find()) {
      throw new RuntimeException("Could not find heap size string.");
    }

    String match = m.group();

    // actual values
    val.minHeapSize = valueAfter(match, "Minimum heap ");
    val.initialHeapSize = valueAfter(match, "Initial heap ");
    val.maxHeapSize = valueAfter(match, "Maximum heap ");
    val.minAlignment = valueAfter(match, "Min alignment ");
    val.maxAlignment = valueAfter(match, "Max alignment ");
  }

  /**
   * Verify whether the VM automatically synchronizes minimum and initial heap size if only
   * one is given for the GC specified.
   */
  public static void checkErgonomics(String[] args, long[] newoldsize,
    long expectedMin, long expectedInitial) throws Exception {

    MinInitialMaxValues v = new MinInitialMaxValues();
    getMinInitialMaxHeap(args, v);

    if ((expectedMin != -1) && (align_up(expectedMin, v.minAlignment) != v.minHeapSize)) {
      throw new RuntimeException("Actual minimum heap size of " + v.minHeapSize +
        " differs from expected minimum heap size of " + expectedMin);
    }

    if ((expectedInitial != -1) && (align_up(expectedInitial, v.minAlignment) != v.initialHeapSize)) {
      throw new RuntimeException("Actual initial heap size of " + v.initialHeapSize +
        " differs from expected initial heap size of " + expectedInitial);
    }

    // always check the invariant min <= initial <= max heap size
    if (!(v.minHeapSize <= v.initialHeapSize && v.initialHeapSize <= v.maxHeapSize)) {
      throw new RuntimeException("Inconsistent min/initial/max heap sizes, they are " +
        v.minHeapSize + "/" + v.initialHeapSize + "/" + v.maxHeapSize);
    }
  }

  /**
   * Verify whether the VM respects the given maximum heap size in MB for the
   * GC specified.
   * @param gcflag The garbage collector to test as command line flag. E.g. -XX:+UseG1GC
   * @param maxHeapSize the maximum heap size to verify, in MB.
   */
  public static void checkGenMaxHeapSize(String gcflag, long maxHeapsize) throws Exception {
    final long K = 1024;

    MinInitialMaxValues v = new MinInitialMaxValues();
    getMinInitialMaxHeap(new String[] { gcflag, "-XX:MaxHeapSize=" + maxHeapsize + "M" }, v);

    long expectedHeapSize = align_up(maxHeapsize * K * K, v.maxAlignment);
    long actualHeapSize = v.maxHeapSize;

    if (actualHeapSize > expectedHeapSize) {
      throw new RuntimeException("Heap has " + actualHeapSize  +
        " bytes, expected to be less than " + expectedHeapSize);
    }
  }

  private static long getFlagValue(String flag, String where) {
    Matcher m = Pattern.compile(flag + "\\s+:?=\\s+\\d+").matcher(where);
    if (!m.find()) {
      throw new RuntimeException("Could not find value for flag " + flag + " in output string");
    }
    String match = m.group();
    return Long.parseLong(match.substring(match.lastIndexOf(" ") + 1, match.length()));
  }

  private static void shouldContainOrNot(OutputAnalyzer output, boolean contains, String message) throws Exception {
    if (contains) {
      output.shouldContain(message);
    } else {
      output.shouldNotContain(message);
    }
  }

  private static void expect(String[] flags, boolean hasWarning, boolean hasError, int errorcode) throws Exception {
    ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(flags);
    OutputAnalyzer output = new OutputAnalyzer(pb.start());
    shouldContainOrNot(output, hasWarning, "Warning");
    shouldContainOrNot(output, hasError, "Error");
    output.shouldHaveExitValue(errorcode);
  }

  private static void expectError(String[] flags) throws Exception {
    expect(flags, false, true, 1);
  }

  private static void expectValid(String[] flags) throws Exception {
    expect(flags, false, false, 0);
  }
}

