/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.memory;

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.memory.HeapBlock;
import sun.jvm.hotspot.memory.VirtualSpace;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;

public class CodeHeap extends VMObject {
  private static Field         memoryField;
  private static Field         segmapField;
  //  private static CIntegerField numberOfCommittedSegmentsField;
  //  private static CIntegerField numberOfReservedSegmentsField;
  //  private static CIntegerField segmentSizeField;
  private static CIntegerField log2SegmentSizeField;
  //  private static CIntegerField nextSegmentField;
  //  private static AddressField  freelistField;
  //  private static CIntegerField freeSegmentsField;

  private sun.jvm.hotspot.memory.VirtualSpace memory;
  private sun.jvm.hotspot.memory.VirtualSpace segmentMap;
  private int log2SegmentSize;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static void initialize(TypeDataBase db) {
    Type type = db.lookupType("CodeHeap");

    memoryField          = type.getField("_memory");
    segmapField          = type.getField("_segmap");
    log2SegmentSizeField = type.getCIntegerField("_log2_segment_size");

  }

  public CodeHeap(Address addr) {
    super(addr);
    log2SegmentSize = (int) log2SegmentSizeField.getValue(addr);
    segmentMap = new sun.jvm.hotspot.memory.VirtualSpace(addr.addOffsetTo(segmapField.getOffset()));
    memory = new sun.jvm.hotspot.memory.VirtualSpace(addr.addOffsetTo(memoryField.getOffset()));
  }

  public Address begin() {
    return getMemory().low();
  }

  public Address end() {
    return getMemory().high();
  }

  public boolean contains(Address p) {
    return (begin().lessThanOrEqual(p) && end().greaterThan(p));
  }

  /** Returns the start of the block containing p or null */
  public Address findStart(Address p) {
    if (!contains(p)) return null;
    HeapBlock h = blockStart(p);
    if (h == null || h.isFree()) {
      return null;
    }
    return h.getAllocatedSpace();
  }

  public Address nextBlock(Address ptr) {
    Address base = blockBase(ptr);
    if (base == null) {
      return null;
    }
    HeapBlock block = getBlockAt(base);
    return base.addOffsetTo(block.getLength() * (1 << getLog2SegmentSize()));
  }

  //--------------------------------------------------------------------------------
  // Internals only below this point
  //

  private sun.jvm.hotspot.memory.VirtualSpace getMemory() {
    return memory;
  }

  private VirtualSpace getSegmentMap() {
    return segmentMap;
  }

  private long segmentFor(Address p) {
    return p.minus(getMemory().low()) >> getLog2SegmentSize();
  }

  private int getLog2SegmentSize() {
    return log2SegmentSize;
  }

  private HeapBlock getBlockAt(Address addr) {
    return (HeapBlock) VMObjectFactory.newObject(HeapBlock.class, addr);
  }


  private HeapBlock blockStart(Address p) {
    Address base = blockBase(p);
    if (base == null) return null;
    return getBlockAt(base);
  }

  private Address blockBase(Address p) {
    long i = segmentFor(p);
    Address b = getSegmentMap().low();
    if (b.getCIntegerAt(i, 1, true) == 0xFF) {
      return null;
    }
    while (b.getCIntegerAt(i, 1, true) > 0) {
      i -= b.getCIntegerAt(i, 1, true);
    }
    return getMemory().low().addOffsetTo(i << getLog2SegmentSize());
  }

}
