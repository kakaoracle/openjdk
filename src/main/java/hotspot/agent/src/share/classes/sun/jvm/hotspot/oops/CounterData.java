/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.oops;

import java.io.*;
import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.oops.BitData;
import sun.jvm.hotspot.oops.DataLayout;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.utilities.*;

// CounterData
//
// A CounterData corresponds to a simple counter.
public class CounterData extends BitData {

  static final int countOff = 0;
  static final int counterCellCount = 1;

  public CounterData(DataLayout layout) {
    super(layout);
  }

  static int staticCellCount() {
    return counterCellCount;
  }

  public int cellCount() {
    return staticCellCount();
  }

  // Direct accessor
  int count() {
    return uintAt(countOff);
  }

  // Code generation support
  static int countOffset() {
    return cellOffset(countOff);
  }
  static int counterDataSize() {
    return cellOffset(counterCellCount);
  }

  public void printDataOn(PrintStream st) {
    printShared(st, "CounterData");
    st.println("count(" + count() + ")");
  }
}
