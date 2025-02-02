/*
 * Copyright (c) 2001, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.win32.coff;

import sun.jvm.hotspot.debugger.win32.coff.DebugVC50SegDesc;
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50Subsection;

/** Models the "sstSegMap" subsection in Visual C++ 5.0 debug
    information. This subsection contains the mapping between the
    logical segment indices used in the symbol table and the physical
    segments where the program was loaded. There is one sstSegMap per
    executable or DLL. (Some of the descriptions are taken directly
    from Microsoft's documentation and are copyrighted by Microsoft.) */

public interface DebugVC50SSSegMap extends DebugVC50Subsection {
  /** Count of the number of segment descriptors in table. */
  public short getNumSegDesc();

  /** The total number of logical segments. All group descriptors
      follow the logical segment descriptors. The number of group
      descriptors is given by <i>cSeg - cSegLog</i>. */
  public short getNumLogicalSegDesc();

  /** Get the <i>i</i>th segment descriptor (0..getNumSegDesc() -
      1). */
  public DebugVC50SegDesc getSegDesc(int i);
}
