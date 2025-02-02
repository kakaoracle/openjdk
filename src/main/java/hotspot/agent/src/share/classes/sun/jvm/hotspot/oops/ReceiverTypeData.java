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
import sun.jvm.hotspot.oops.CounterData;
import sun.jvm.hotspot.oops.DataLayout;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.oops.Metadata;
import sun.jvm.hotspot.oops.MethodData;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.utilities.*;

// ReceiverTypeData
//
// A ReceiverTypeData is used to access profiling information about a
// dynamic type check.  It consists of a counter which counts the total times
// that the check is reached, and a series of (Klass, count) pairs
// which are used to store a type profile for the receiver of the check.
public class ReceiverTypeData extends CounterData {
  static final int   receiver0Offset = counterCellCount;
  static final int     count0Offset = receiver0Offset + 1;
  static final int     receiverTypeRowCellCount = (count0Offset + 1) - receiver0Offset;

  public ReceiverTypeData(DataLayout layout) {
    super(layout);
    //assert(layout.tag() == DataLayout.receiverTypeDataTag ||
    //       layout.tag() == DataLayout.virtualCallDataTag, "wrong type");
  }

  boolean isReceivertypedata() { return true; }

  static int staticCellCount() {
    return counterCellCount + sun.jvm.hotspot.oops.MethodData.TypeProfileWidth * receiverTypeRowCellCount;
  }

  public int cellCount() {
    return staticCellCount();
  }

  // Direct accessors
  public static int rowLimit() {
    return MethodData.TypeProfileWidth;
  }
  public static int receiverCellIndex(int row) {
    return receiver0Offset + row * receiverTypeRowCellCount;
  }
  public static int receiverCountCellIndex(int row) {
    return count0Offset + row * receiverTypeRowCellCount;
  }

  // Get the receiver at row.  The 'unchecked' version is needed by parallel old
  // gc; it does not assert the receiver is a klass.  During compaction of the
  // perm gen, the klass may already have moved, so the isKlass() predicate
  // would fail.  The 'normal' version should be used whenever possible.
  sun.jvm.hotspot.oops.Klass receiverUnchecked(int row) {
    //assert(row < rowLimit(), "oob");
    Address recv = addressAt(receiverCellIndex(row));
    return (sun.jvm.hotspot.oops.Klass) Metadata.instantiateWrapperFor(recv);
  }

  public sun.jvm.hotspot.oops.Klass receiver(int row) {
    Klass recv = receiverUnchecked(row);
    //assert(recv == NULL || ((oop)recv).isKlass(), "wrong type");
    return recv;
  }

  public int receiverCount(int row) {
    //assert(row < rowLimit(), "oob");
    return uintAt(receiverCountCellIndex(row));
  }

  // Code generation support
  static int receiverOffset(int row) {
    return cellOffset(receiverCellIndex(row));
  }
  static int receiverCountOffset(int row) {
    return cellOffset(receiverCountCellIndex(row));
  }
  static int receiverTypeDataSize() {
    return cellOffset(staticCellCount());
  }

  void printReceiverDataOn(PrintStream st) {
    int row;
    int entries = 0;
    for (row = 0; row < rowLimit(); row++) {
      if (receiver(row) != null)  entries++;
    }
    st.println("count(" + count() + ") entries(" + entries + ")");
    for (row = 0; row < rowLimit(); row++) {
      if (receiver(row) != null) {
        tab(st);
        receiver(row).printValueOn(st);
        st.println("(" + receiverCount(row) + ")");
      }
    }
  }
  public void printDataOn(PrintStream st) {
    printShared(st, "ReceiverTypeData");
    printReceiverDataOn(st);
  }
}
