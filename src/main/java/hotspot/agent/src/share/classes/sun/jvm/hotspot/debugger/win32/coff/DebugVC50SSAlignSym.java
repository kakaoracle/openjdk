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

import sun.jvm.hotspot.debugger.win32.coff.DebugVC50Subsection;
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50SymbolIterator;

/** Models the "sstAlignSym" subsection in Visual C++ 5.0 debug
    information. This subsection apparently contains non-global
    symbols left over from packing into the sstGlobalSym
    subsection. Until we understand the contents of the sstGlobalSym
    subsection, this subsection will contain no accessors. */

public interface DebugVC50SSAlignSym extends DebugVC50Subsection {
  public DebugVC50SymbolIterator getSymbolIterator();
}
